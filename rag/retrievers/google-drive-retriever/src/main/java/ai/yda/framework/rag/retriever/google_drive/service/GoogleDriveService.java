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
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentMetadataPort;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentMetadataMapper;
import ai.yda.framework.rag.retriever.google_drive.processor.DocumentProcessorProvider;

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

    private final DocumentProcessorProvider documentProcessor;

    private final DocumentMetadataMapper documentMetadataMapper;

    /**
     * Constructs a new instance of {@link GoogleDriveService}.
     * Initializes the Google Drive API client using the provided Service Account JSON InputStream.
     *
     * @param credentialsStream the InputStream containing the Service Account JSON key file.
     * @throws IOException              if an I/O error occurs while reading the Service Account key file.
     * @throws GeneralSecurityException if a security error occurs during Google API client initialization.
     */
    public GoogleDriveService(final @NonNull InputStream credentialsStream, final @NonNull String driveId, final @NonNull DocumentMetadataPort documentMetadataPort,
                              final DocumentProcessorProvider documentProcessor,
                              final DocumentMetadataMapper documentMetadataMapper)
            throws IOException, GeneralSecurityException {

        this.documentMetadataPort = documentMetadataPort;

        var credentials =
                GoogleCredentials.fromStream(credentialsStream).createScoped(Collections.singleton(DriveScopes.DRIVE));

        this.driveService = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(GOOGLE_DRIVE_APP_NAME)
                .build();
        this.documentProcessor = documentProcessor;
        this.documentMetadataMapper = documentMetadataMapper;

        this.driveId = driveId;
        log.info("Google Drive service initialized successfully.");
    }

    public List<DocumentMetadataEntity> processFiles() throws IOException {
        var documentMetadataEntities = new ArrayList<DocumentMetadataEntity>();
        for (File file : listFiles()) {
            try (InputStream inputStream =
                    driveService.files().get(file.getId()).executeMediaAsInputStream()) {
                var documentMetadata = documentMetadataMapper.toEntity(file);
                var contentEntities =
                        documentProcessor.processDocument(file.getFileExtension(), inputStream, documentMetadata);
                documentMetadata.setDocumentContents(contentEntities);
                documentMetadataEntities.add(documentMetadata);
            }
        }
        return documentMetadataEntities;
    }

    // TODO: add drive id configuration
    private List<File> listFiles() throws IOException {
        var result = driveService
                .files()
                .list()
                .setSupportsAllDrives(true)
                .setIncludeItemsFromAllDrives(true)
                .setCorpora("drive")
                .setDriveId(driveId)
                .setFields("files(id,name,description,webViewLink,createdTime,modifiedTime,mimeType,fileExtension)")
                .setPageSize(10)
                .setQ("mimeType != 'application/vnd.google-apps.folder'")
                .execute();

        var files = result.getFiles();

        if (files == null || files.isEmpty()) {
            log.debug("No files found.");
            return new ArrayList<>();
        } else {
            for (var file : files) {
                log.debug("Processing file: {}", file.getName());
            }
            return files;
        }
    }

    public void syncDrive(String driveId) throws IOException {
        // 1. Retrieve all files/folders from this Drive.
        var query = String.format("'%s' in parents and trashed = false", driveId);

        var result = driveService
                .files()
                .list()
                .setSupportsAllDrives(true)
                .setIncludeItemsFromAllDrives(true)
                .setCorpora("drive")
                .setDriveId(driveId)
                .setFields(
                        "files(id,name,mimeType,description,webViewLink,createdTime,modifiedTime)") // Include MIME type
                .setQ(query)
                .setPageSize(100)
                .execute();

        var driveFiles = result.getFiles();

        // 2. Iterate through the files to create/update DocumentMetadataEntity
        for (var file : driveFiles) {
            String documentId = file.getId();
            var entity = documentMetadataPort
                    .findById(documentId)
                    .orElseGet(DocumentMetadataEntity::new);

            entity.setDocumentId(documentId);
            entity.setName(file.getName());
            entity.setDescription(file.getDescription());
            entity.setUri(file.getWebViewLink());  // or alternate link
            entity.setCreatedAt(file.getCreatedTime() != null
                    ? Instant.ofEpochMilli(file.getCreatedTime().getValue()).atOffset(ZoneOffset.UTC)
                    : OffsetDateTime.now());

            entity.setModifiedAt(file.getModifiedTime() != null
                    ? Instant.ofEpochMilli(file.getModifiedTime().getValue()).atOffset(ZoneOffset.UTC)
                    : OffsetDateTime.now());
            entity.setMimeType(file.getMimeType());
            entity.setDriveId(driveId);

            // If file has a parent, set the parentDocumentId
            // Note: This depends on how you're fetching file info.
            // Google Drive returns a list of parent IDs, but for a hierarchical structure,
            // you often only have one parent for a file/folder.
            var parents = file.getParents();
            if (parents != null && !parents.isEmpty()) {
                // Get the parent ID from the Google Drive 'parents' list
                String parentId = parents.get(0);

                // 1. Look up the parent entity in your repository
                DocumentMetadataEntity parentEntity = documentMetadataPort
                        .findById(parentId)
                        // 2. If the parent isn't in the DB yet, create it (or skip if you only handle known parents)
                        .orElseGet(() -> {
                            DocumentMetadataEntity newParent = new DocumentMetadataEntity();
                            newParent.setDocumentId(parentId);
                            // Optionally, initialize some default fields
                            return documentMetadataPort.save(newParent);
                        });

                // 3. Set the parent on the current entity
                entity.setParent(parentEntity);
            } else {
                // This entity has no parent (e.g., a root folder)
                entity.setParent(null);
            }
            // Save metadata
            documentMetadataPort.save(entity);

            // 3. If you wanted to store content, you'd handle that here.
            // For now, it's purely metadata, so skip content logic.

        }
    }


    public void listFilesInDirectory() throws IOException {
        // Query to filter files by the given directory
        var query = String.format("'%s' in parents and trashed = false", driveId);

        var result = driveService
                .files()
                .list()
                .setSupportsAllDrives(true)
                .setIncludeItemsFromAllDrives(true)
                .setCorpora("drive")
                .setDriveId(driveId)
                .setFields(
                        "files(id,name,mimeType,description,webViewLink,createdTime,modifiedTime)") // Include MIME type
                .setQ(query)
                .setPageSize(100)
                .execute();

        var files = result.getFiles();

        if (files == null || files.isEmpty()) {
            log.info("No files found in directory with ID: {}", driveId);
        } else {
            log.info("Files in directory with ID {}:", driveId);
            for (var file : files) {
                var type = "File";
                if ("application/vnd.google-apps.folder".equals(file.getMimeType())) {
                    type = "Folder";
                }
                log.info(
                        "Type: {}, Name: {}, ID: {}, Description: {}, Link: {}, Created: {}, Modified: {}",
                        type,
                        file.getName(),
                        file.getId(),
                        file.getDescription(),
                        file.getWebViewLink(),
                        file.getCreatedTime(),
                        file.getModifiedTime());
            }
        }
    }
}
