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

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentMetadataMapper;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentMetadataPort;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    private final VectorStore vectorStore;

    private final DocumentMetadataPort documentMetadataPort;

    private final DocumentProcessorProvider documentProcessor;

    private final DocumentMetadataMapper documentMetadataMapper;

    private final DocumentSummaryService documentSummaryService;

    /**
     * Constructs a new instance of {@link GoogleDriveService}.
     * Initializes the Google Drive API client using the provided Service Account JSON InputStream.
     *
     * @param credentialsStream the InputStream containing the Service Account JSON key file.
     * @throws IOException              if an I/O error occurs while reading the Service Account key file.
     * @throws GeneralSecurityException if a security error occurs during Google API client initialization.
     */
    public GoogleDriveService(
            final @NonNull InputStream credentialsStream,
            final @NonNull String driveId,
            final @NonNull DocumentMetadataPort documentMetadataPort,
            final @NonNull DocumentProcessorProvider documentProcessor,
            final @NonNull DocumentMetadataMapper documentMetadataMapper,
            final @NonNull VectorStore vectorStore,
            final @NonNull DocumentSummaryService documentSummaryService)
            throws IOException, GeneralSecurityException {

        this.documentMetadataPort = documentMetadataPort;
        this.documentProcessor = documentProcessor;
        this.documentMetadataMapper = documentMetadataMapper;
        this.driveId = driveId;
        this.vectorStore = vectorStore;
        this.documentSummaryService = documentSummaryService;

        var credentials =
                GoogleCredentials.fromStream(credentialsStream).createScoped(Collections.singleton(DriveScopes.DRIVE));

        this.driveService = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(GOOGLE_DRIVE_APP_NAME)
                .build();

        log.info("Google Drive service initialized successfully.");
    }

    public void saveToVectorStore(final List<DocumentMetadataEntity> documents) {
        var summarizedDocuments = documentSummaryService.summarizeDocuments(documents);
        var documentIds = summarizedDocuments.stream().map(Document::getId).toList();
        Objects.requireNonNull(vectorStore.delete(documentIds)).ifPresent(deleted -> {
            if (!deleted) {
                throw new RuntimeException("Failed to delete document");
            }
        });
        vectorStore.add(summarizedDocuments);
    }

    public List<DocumentMetadataEntity> syncDriveAndProcessDocuments() throws IOException {

        var documentMetadataEntities = new ArrayList<DocumentMetadataEntity>();

        for (var file : listFiles()) {

            var mappedEntity = documentMetadataMapper.toEntity(file);

            var documentMetadataEntity = documentMetadataPort
                    .findById(mappedEntity.getDocumentId())
                    .map(existing -> documentMetadataMapper.updateEntity(mappedEntity, existing))
                    .orElse(mappedEntity);

            documentMetadataEntity.setParent(resolveParent(file));

            // Fetch and process file content
            try (var inputStream = driveService.files().get(file.getId()).executeMediaAsInputStream()) {
                var contentEntities =
                        documentProcessor.processDocument(file.getFileExtension(), inputStream, documentMetadataEntity);
                documentMetadataEntity.setDocumentContents(contentEntities);
            } catch (IOException e) {
                log.error("Failed to retrieve content for file: {}", file.getId(), e);
            }

            documentMetadataPort.save(documentMetadataEntity);
            documentMetadataEntities.add(documentMetadataEntity);
        }

        return documentMetadataEntities;
    }

    /**
     * Fetches (or creates) the parent DocumentMetadataEntity for a Google Drive file.
     * If the file has no parents, returns null.
     */
    private DocumentMetadataEntity resolveParent(File file) {
        var parents = file.getParents();
        if (parents != null && !parents.isEmpty()) {
            var parentId = parents.get(0); // typically one parent for standard Drive structure
            return documentMetadataPort.findById(parentId).orElseGet(() -> {
                var newParent = new DocumentMetadataEntity();
                newParent.setDocumentId(parentId);
                // Optionally set default fields for the parent here
                return documentMetadataPort.save(newParent);
            });
        }
        // No parent
        return null;
    }

    private List<File> listFiles() throws IOException {
        var result = driveService
                .files()
                .list()
                .setSupportsAllDrives(true)
                .setIncludeItemsFromAllDrives(true)
                .setCorpora("drive")
                .setDriveId(driveId)
                .setFields("files(id,name,description,webViewLink,createdTime,modifiedTime,mimeType,fileExtension)")
                .setPageSize(100)
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
}