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
import java.util.Optional;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentMetadataDTO;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentMetadataMapper;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentContentPort;
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

    private final DocumentMetadataPort documentMetadataPort;

    private final DocumentContentPort documentContentPort;

    private final DocumentProcessorProvider documentProcessor;

    private final DocumentMetadataMapper documentMetadataMapper;

    private final DocumentSummaryService documentSummaryService;

    private final ChunkingService chunkingService;

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
            final @NonNull DocumentContentPort documentContentPort,
            final @NonNull DocumentProcessorProvider documentProcessor,
            final @NonNull DocumentMetadataMapper documentMetadataMapper,
            final @NonNull DocumentSummaryService documentSummaryService,
            final @NonNull ChunkingService chunkingService)
            throws IOException, GeneralSecurityException {

        this.documentMetadataPort = documentMetadataPort;
        this.documentContentPort = documentContentPort;
        this.documentProcessor = documentProcessor;
        this.documentMetadataMapper = documentMetadataMapper;
        this.driveId = driveId;
        this.documentSummaryService = documentSummaryService;
        this.chunkingService = chunkingService;

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

    // TODO: update document metadata and content only if modifiedAt stored in db is not the same as file modifiedTime
    public void syncDriveAndProcessDocuments() throws IOException {
        documentMetadataPort.deleteAll();

        for (var file : listFiles()) {
            var documentMetadataDTO = documentMetadataMapper.toDTO(file);

            var parent = resolveParent(file);
            if (parent != null) {
                documentMetadataDTO.setParentId(parent.getDocumentId());
            }

            if (!documentMetadataDTO.isFolder()) {
                try (var inputStream = driveService.files().get(file.getId()).executeMediaAsInputStream()) {

                    var contentEntities = documentProcessor.processDocument(
                            file.getFileExtension(), inputStream, documentMetadataDTO.getDocumentId());

                    documentMetadataDTO.setDocumentContents(contentEntities);
                    documentMetadataDTO.setSummary(documentSummaryService.summarizeDocument(documentMetadataDTO));
                }
            }

            if (file.getFileExtension() != null && !file.getFileExtension().contains("xlsx")) {
                documentMetadataDTO = chunkingService.processContent(documentMetadataDTO, file.getFileExtension());
            }
            documentMetadataPort.save(documentMetadataDTO);
        }
    }

    /**
     * Fetches (or creates) the parent DocumentMetadataEntity for a Google Drive file.
     * If the file has no parents, returns null.
     */
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
                        .setFields("files(id,name,parents,description,driveId,webViewLink,createdTime,modifiedTime,"
                                + "mimeType,fileExtension)")
                        .setPageSize(100)
                        .execute()
                        .getFiles())
                .orElseGet(Collections::emptyList);
    }
}
