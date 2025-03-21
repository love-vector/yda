/*
 * YDA - Open-Source Java AI Assistant.
 * Copyright (C) 2024 Love Vector OÜ <https://vector-inc.dev/>

 * This file is part of YDA.

 * YDA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * YDA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with YDA.  If not, see <https://www.gnu.org/licenses/>.
*/
package ai.yda.framework.rag.retriever.google_drive.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;

import ai.yda.framework.rag.retriever.google_drive.constants.FileChangeType;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentMetadataDTO;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentMetadataMapper;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentMetadataPort;
import ai.yda.framework.rag.retriever.google_drive.service.document.processor.DocumentProcessorProvider;


@Slf4j
public class DriveWebhookService {
    private final DocumentMetadataPort documentMetadataPort;
    private final Drive driveSync;
    private final String driveId;
    private final String webhookReceiverUrl;
    private final DocumentProcessorProvider documentProcessor;
    private final DocumentMetadataMapper documentMetadataMapper;
    private final DocumentAiDescriptionService documentAiDescriptionService;
    private final ResourceLoader resourceLoader;
    private final String oauthClientSecretsPath;
    private final String tokenPath;
    private volatile String startPageToken;

    private final Map<String, FileChangeType> fileChangeMap = new ConcurrentHashMap<>();

    public DriveWebhookService(
            final DocumentMetadataPort documentMetadataPort,
            final String driveId,
            final String webhookReceiverUrl,
            final DocumentProcessorProvider documentProcessor,
            final DocumentMetadataMapper documentMetadataMapper,
            final DocumentAiDescriptionService documentAiDescriptionService,
            final ResourceLoader resourceLoader,
            final String oauthClientSecretsPath,
            final String tokenPath) {
        this.documentMetadataPort = documentMetadataPort;
        this.driveId = driveId;
        this.webhookReceiverUrl = webhookReceiverUrl;
        this.documentProcessor = documentProcessor;
        this.documentMetadataMapper = documentMetadataMapper;
        this.documentAiDescriptionService = documentAiDescriptionService;
        this.resourceLoader = resourceLoader;
        this.oauthClientSecretsPath = oauthClientSecretsPath;
        this.tokenPath = tokenPath;

        try {
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            InputStream in = resourceLoader.getResource(oauthClientSecretsPath).getInputStream();
            var clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

            FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new java.io.File(tokenPath));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(),
                            GsonFactory.getDefaultInstance(),
                            clientSecrets,
                            List.of(DriveScopes.DRIVE))
                    .setDataStoreFactory(dataStoreFactory)
                    .setAccessType("offline")
                    .build();

            var credential = flow.loadCredential("user");
            this.driveSync = new Drive.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("YDA Google Drive Channel")
                    .build();
        } catch (Exception e) {
            log.info("Initializing Google Drive Webhook Service failed", e);
            throw new RuntimeException(e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        this.startPageToken = initializeStartPageToken();
        setupDriveWatchChannel();
    }

    // что следует сделать с операцииям UNTRASH и TRASH
    @Scheduled(fixedRate = 900_000)
    private void syncChanges() {
        if (fileChangeMap.isEmpty()) {
            log.info("No file changes to sync.");
            return;
        }
        Map<String, FileChangeType> changesToProcess = new HashMap<>(fileChangeMap);
        fileChangeMap.clear();

        changesToProcess.forEach((fileId, changeType) -> {
            try {
                var file = driveSync.files().get(fileId).execute();
                switch (changeType) {
                    case REMOVE -> {
                        log.info("Deleting file with ID={}", fileId);
                        documentMetadataPort.deleteById(fileId);
                    }
                    case UPDATE, CHANGE -> {
                        log.info("Update file with ID={}", fileId);
                        documentMetadataPort.deleteById(fileId);
                        saveFile(file);
                    }
                    case ADD -> {
                        log.info("Adding file with ID={}", fileId);
                        saveFile(file);
                    }
                }
            } catch (IOException e) {
                log.error("Failed to process file ID={}", fileId, e);
            }
        });
    }

    private void saveFile(final File file) throws IOException {
        var documentMetadataDTO = documentMetadataMapper.toDTO(file);
        var parent = resolveParent(file);
        if (parent != null) {
            documentMetadataDTO.setParentId(parent.getDocumentId());
        }
        if (!documentMetadataDTO.isFolder()) {
            try (var inputStream = driveSync.files().get(file.getId()).executeMediaAsInputStream()) {
                var contentEntities =
                        documentProcessor.processDocument(file.getFileExtension(), inputStream, documentMetadataDTO);
                documentMetadataDTO.setDocumentContents(contentEntities);
                documentMetadataDTO.setAiDescription(
                        documentAiDescriptionService.generateAiDescription(documentMetadataDTO));
            }
        }
        documentMetadataPort.save(documentMetadataDTO);
    }

    private DocumentMetadataDTO resolveParent(final File file) throws IOException {
        var parents = file.getParents();
        if (parents != null && !parents.isEmpty()) {
            var parentId = parents.get(0);

            var existingParent = documentMetadataPort.findById(parentId);

            if (existingParent.isPresent()) {
                return existingParent.get();
            }

            var parentFile = driveSync
                    .files()
                    .get(parentId)
                    .setSupportsAllDrives(true)
                    .setFields(
                            "id,name,parents,description,driveId,webViewLink,createdTime,modifiedTime,mimeType,fileExtension")
                    .execute();

            return documentMetadataPort.save(documentMetadataMapper.toDTO(parentFile));
        }
        return null;
    }

    private String initializeStartPageToken() {
        try {
            StartPageToken response = driveSync
                    .changes()
                    .getStartPageToken()
                    .setDriveId(driveId)
                    .setSupportsAllDrives(true)
                    .execute();
            return response.getStartPageToken();
        } catch (Exception e) {
            log.error("Failed to get initial startPageToken", e);
        }
        return null;
    }

    public void processWebhook() {
        try {
            ChangeList changeList = driveSync
                    .changes()
                    .list(startPageToken)
                    .setDriveId(driveId)
                    .setSupportsAllDrives(true)
                    .setIncludeItemsFromAllDrives(true)
                    .execute();

            for (Change change : changeList.getChanges()) {
                var fileId = change.getFileId();
                var effectiveChangeType = resolveFileChangeType(change);

                fileChangeMap.merge(fileId, effectiveChangeType, this::resolvePriority);
                log.debug("Registered change: fileId={}, type={}", fileId, effectiveChangeType);
            }

            var newToken = changeList.getNewStartPageToken();
            if (newToken != null) {
                this.startPageToken = newToken;
                log.debug("Updated startPageToken to {}", newToken);
            }

        } catch (Exception e) {
            log.error("Error handling webhook changes", e);
        }
    }

    private void setupDriveWatchChannel() {
        var channel = new Channel()
                .setId(UUID.randomUUID().toString())
                .setType("web_hook")
                .setAddress(webhookReceiverUrl)
                .setExpiration(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));

        try {
            driveSync
                    .changes()
                    .watch(startPageToken, channel)
                    .setDriveId(driveId)
                    .setIncludeItemsFromAllDrives(true)
                    .setSupportsAllDrives(true)
                    .execute();
            log.info("Subscribed successfully to Google Drive webhook (Shared Drive).");
        } catch (Exception e) {
            log.error("Error subscribing to Google Drive webhook", e);
        }
    }

    private FileChangeType resolveFileChangeType(final Change change) {
        if (Boolean.TRUE.equals(change.getRemoved())) {
            return FileChangeType.REMOVE;
        }

        File file = change.getFile();
        if (file == null) {
            return FileChangeType.CHANGE;
        }

        var createdTime = file.getCreatedTime();
        var modifiedTime = file.getModifiedTime();

        if (createdTime != null && modifiedTime != null) {
            if (createdTime.getValue() == modifiedTime.getValue()) {
                return FileChangeType.ADD;
            } else {
                return FileChangeType.UPDATE;
            }
        }

        return FileChangeType.CHANGE;
    }

    private FileChangeType resolvePriority(FileChangeType oldType, FileChangeType newType) {
        return newType == FileChangeType.REMOVE
                ? FileChangeType.REMOVE
                : (oldType == FileChangeType.ADD && newType == FileChangeType.UPDATE ? FileChangeType.ADD : newType);
    }
}
