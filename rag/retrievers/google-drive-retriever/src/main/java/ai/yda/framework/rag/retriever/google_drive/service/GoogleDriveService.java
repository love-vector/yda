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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentContentEntity;
import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentMetadataMapper;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentMetadataPort;

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

    public void saveToVectorStore() {
        var summarizedDocuments = documentSummaryService.summarizeDocuments(documentMetadataPort.findAll());
        var documentIds = summarizedDocuments.stream().map(Document::getId).toList();
        Objects.requireNonNull(vectorStore.delete(documentIds)).ifPresent(deleted -> {
            if (!deleted) {
                throw new RuntimeException("Failed to update document in Vector store");
            }
        });
        vectorStore.add(summarizedDocuments);
    }

    public List<DocumentContentEntity> findRetrievedDocuments(final List<String> documentIds) {
        return documentIds.stream()
                .map(documentMetadataPort::findById)
                .flatMap(Optional::stream)
                .flatMap(entity -> entity.getDocumentContents().stream())
                .toList();
    }

    // TODO: update document metadata and content only if modifiedAt stored in db is not the same as file modifiedTime
    public void syncDriveAndProcessDocuments() throws IOException {
        documentMetadataPort.deleteAll();

        for (var file : listFiles()) {
            var documentMetadataEntity = documentMetadataMapper.toEntity(file);
            documentMetadataEntity.setParent(resolveParent(file));

            // Fetch and process file content
            if (!documentMetadataEntity.isFolder()) {
                try (var inputStream = driveService.files().get(file.getId()).executeMediaAsInputStream()) {
                    var contentEntities = documentProcessor.processDocument(
                            file.getFileExtension(), inputStream, documentMetadataEntity);
                    documentMetadataEntity.setDocumentContents(contentEntities);
                }
            }

            documentMetadataPort.save(documentMetadataEntity);
        }
    }

    /**
     * Fetches (or creates) the parent DocumentMetadataEntity for a Google Drive file.
     * If the file has no parents, returns null.
     */
    private DocumentMetadataEntity resolveParent(final File file) throws IOException {
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
                            "id,name,parents,description,webViewLink,createdTime,modifiedTime,mimeType,fileExtension")
                    .execute();

            return documentMetadataPort.save(documentMetadataMapper.toEntity(parentFile));
        }
        return null; // No parent
    }

    private List<File> listFiles() throws IOException {
        return Optional.ofNullable(driveService
                        .files()
                        .list()
                        .setSupportsAllDrives(true)
                        .setIncludeItemsFromAllDrives(true)
                        .setCorpora("drive")
                        .setDriveId(driveId)
                        .setFields("files(id,name,parents,description,webViewLink,createdTime,modifiedTime,"
                                + "mimeType,fileExtension)")
                        .setPageSize(100)
                        .execute()
                        .getFiles())
                .orElseGet(Collections::emptyList);
    }
}
