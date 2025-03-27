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

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Channel;
import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentMetadataDTO;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentMetadataMapper;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentContentPort;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentMetadataPort;
import ai.yda.framework.rag.retriever.google_drive.service.document.processor.DocumentProcessorProvider;

/**
 * Service class for interacting with Google Drive using a Service Account.
 * This service initializes using a Service Account JSON key provided as an {@link InputStream}.
 *
 * @author dmmrch
 * @since 0.2.0
 */
@Slf4j
public class GoogleDriveService {

    private static final String GOOGLE_DRIVE_APP_NAME = "YDA Google Drive Retriever";

    private final Drive driveService;
    private final String driveId;

    private final DocumentMetadataPort documentMetadataPort;

    private final DocumentContentPort documentContentPort;

    private final DocumentProcessorProvider documentProcessor;

    private final DocumentMetadataMapper documentMetadataMapper;

    private final DocumentAiDescriptionService documentAiDescriptionService;

    private final String tokenPath;

    private final String webhookReceiverUrl;

    private volatile String startPageToken;

    private final Map<String, File> pendingFileChanges = new ConcurrentHashMap<>();

    private Channel currentChannel;

    public GoogleDriveService(
            final @NonNull InputStream credentialsStream,
            final @NonNull String driveId,
            final @NonNull DocumentMetadataPort documentMetadataPort,
            final @NonNull DocumentContentPort documentContentPort,
            final @NonNull DocumentProcessorProvider documentProcessor,
            final @NonNull DocumentMetadataMapper documentMetadataMapper,
            final @NonNull DocumentAiDescriptionService documentAiDescriptionService,
            final @NonNull String tokenPath,
            final @NonNull String webhookReceiverUrl) {

        this.documentMetadataPort = documentMetadataPort;
        this.documentContentPort = documentContentPort;
        this.documentProcessor = documentProcessor;
        this.documentMetadataMapper = documentMetadataMapper;
        this.driveId = driveId;
        this.documentAiDescriptionService = documentAiDescriptionService;
        this.tokenPath = tokenPath;
        this.webhookReceiverUrl = webhookReceiverUrl;
        try {
            var jsonFactory = GsonFactory.getDefaultInstance();
            var clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(credentialsStream));

            var dataStoreFactory = new FileDataStoreFactory(new java.io.File(tokenPath));

            var flow = new GoogleAuthorizationCodeFlow.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(),
                            GsonFactory.getDefaultInstance(),
                            clientSecrets,
                            List.of(DriveScopes.DRIVE))
                    .setDataStoreFactory(dataStoreFactory)
                    .setAccessType("offline")
                    .build();

            var credential = flow.loadCredential("user");
            this.driveService = new Drive.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName("YDA Google Drive Channel")
                    .build();

            this.startPageToken = initializeStartPageToken();
            this.subscribeToDriveChanges();
        } catch (Exception e) {
            log.info("Initializing Google Drive Webhook Service failed", e);
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    private void processPendingChanges() {
        if (pendingFileChanges.isEmpty()) {
            log.info("No file changes to sync.");
            return;
        }

        var changesToProcess = new HashMap<>(pendingFileChanges);
        pendingFileChanges.clear();

        changesToProcess.values().forEach(file -> log.info("Processing file: {}", file.getName()));
        changesToProcess.values().forEach(file -> {
            documentMetadataPort.deleteByIdCascade(file.getId());
            processAndSaveFile(file);
        });
    }

    public void syncDriveAndProcessDocuments() throws IOException {
        documentMetadataPort.deleteAll();
        listFiles().forEach(this::processAndSaveFile);
    }

    private DocumentMetadataDTO resolveParent(final File file) throws IOException {
        var parents = file.getParents();
        if (parents != null && !parents.isEmpty()) {
            var parentId = parents.get(0); // typically one parent for standard Drive structure

            var existingParent = documentMetadataPort.findById(parentId);

            if (existingParent.isPresent()) {
                return existingParent.get();
            }

            var parentFile = driveService
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

    private List<File> listFiles() throws IOException {
        return Optional.ofNullable(driveService
                        .files()
                        .list()
                        .setSupportsAllDrives(true)
                        .setIncludeItemsFromAllDrives(true)
                        .setCorpora("drive")
                        .setDriveId(driveId)
                        .setFields("files(id,name,parents,description,driveId,webViewLink,createdTime,modifiedTime,"
                                + "mimeType,fileExtension)")
                        .setPageSize(1000)
                        .execute()
                        .getFiles())
                .orElseGet(Collections::emptyList);
    }

    private void processAndSaveFile(final File file) {
        try {
            var documentMetadataDTO = documentMetadataMapper.toDTO(file);
            var parent = resolveParent(file);
            if (parent != null) {
                documentMetadataDTO.setParentId(parent.getDocumentId());
            }
            if (!documentMetadataDTO.isFolder()) {
                try (var inputStream = driveService.files().get(file.getId()).executeMediaAsInputStream()) {
                    var contentEntities = documentProcessor.processDocument(
                            file.getFileExtension(), inputStream, documentMetadataDTO);
                    documentMetadataDTO.setDocumentContents(contentEntities);
                    documentMetadataDTO.setAiDescription(
                            documentAiDescriptionService.generateAiDescription(documentMetadataDTO));
                }
            }
            documentMetadataPort.save(documentMetadataDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String initializeStartPageToken() {
        try {
            var response = driveService
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

    public void processWebhook(final String resourceState) throws IOException {
        var changeList = driveService
                .changes()
                .list(startPageToken)
                .setDriveId(driveId)
                .setSupportsAllDrives(true)
                .setIncludeItemsFromAllDrives(true)
                .execute();

        changeList.getChanges().stream()
                .filter(change ->
                        change.getFileId() != null && !change.getFileId().isEmpty() && "change".equals(resourceState))
                .forEach(change -> {
                    var fileId = change.getFileId();

                    if (change.getRemoved()) {
                        documentMetadataPort.deleteByIdCascade(change.getFileId());
                        return;
                    }

                    try {
                        if (!change.getFile().getMimeType().contains("google-apps")) {
                            var file = driveService
                                    .files()
                                    .get(change.getFileId())
                                    .setSupportsAllDrives(true)
                                    .setFields(
                                            "id,name,parents,description,driveId,webViewLink,createdTime,modifiedTime,mimeType,fileExtension,trashed")
                                    .execute();

                            if (!file.getTrashed()
                                    && !"application/vnd.google-apps.folder".equals(file.getMimeType())) {
                                pendingFileChanges.put(file.getId(), file);
                            }
                        }
                    } catch (GoogleJsonResponseException ex) {
                        log.info("File deleted: {}", fileId);
                    } catch (IOException ex) {
                        log.error("I/O error processing change for file {}", fileId, ex);
                    }
                });
        var newToken = changeList.getNewStartPageToken();
        if (newToken != null) {
            this.startPageToken = newToken;
        }
    }

    @Scheduled(fixedDelay = 7, timeUnit = TimeUnit.DAYS)
    private void subscribeToDriveChanges() {
        if (startPageToken == null) {
            log.warn("Start page token is null. Unable to subscribe to Google Drive changes.");
            return;
        }

        unsubscribeFromDriveChanges();
        var channelId = UUID.randomUUID().toString();
        var channel = new Channel()
                .setId(UUID.randomUUID().toString())
                .setType("web_hook")
                .setAddress(webhookReceiverUrl)
                .setExpiration(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));

        try {
            currentChannel = driveService
                    .changes()
                    .watch(startPageToken, channel)
                    .setDriveId(driveId)
                    .setIncludeItemsFromAllDrives(true)
                    .setSupportsAllDrives(true)
                    .execute();
            log.info("Subscribed successfully to Google Drive webhook with channelId: {}", channelId);
        } catch (Exception e) {
            log.error("Error subscribing to Google Drive webhook", e);
        }
    }

    private void unsubscribeFromDriveChanges() {
        if (currentChannel == null) {
            log.warn("No active subscription channel to unsubscribe.");
            return;
        }

        try {
            driveService.channels().stop(currentChannel).execute();
            log.info("Unsubscribed successfully from Google Drive webhook (channelId: {}).", currentChannel.getId());
        } catch (Exception e) {
            log.error("Error unsubscribing from Google Drive webhook (channelId: {}).", currentChannel.getId(), e);
        }
    }
}
