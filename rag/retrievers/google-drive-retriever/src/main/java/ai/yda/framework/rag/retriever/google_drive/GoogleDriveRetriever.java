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
package ai.yda.framework.rag.retriever.google_drive;

import java.io.IOException;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.retriever.google_drive.exception.UnsupportedExtensionException;
import ai.yda.framework.rag.retriever.google_drive.repository.DocumentMetadataRepository;
import ai.yda.framework.rag.retriever.google_drive.service.GoogleDriveService;

/**
 * The {@code GoogleDriveRetriever} class is a Retriever implementation that interacts with Google Drive
 * to retrieve contextual data for a given {@link RagRequest}. It uses the {@link GoogleDriveService}
 * to perform operations on Google Drive.
 *
 * <p>This retriever is designed to process and fetch documents or data stored in Google Drive
 * based on the provided configuration and request context.
 *
 * <p>Usage:
 * - Instantiate this class with the desired `topK` (number of results to retrieve) and a
 * configured {@link GoogleDriveService}.
 * - Use the {@link #retrieve(RagRequest)} method to perform the data retrieval operation.
 *
 * <p>Dependencies:
 * - Requires a properly configured {@link GoogleDriveService} instance.
 *
 * @author dmmrch
 * @since 0.2.0
 */
@Slf4j
public class GoogleDriveRetriever implements Retriever<RagRequest, RagContext> {

    /**
     * The number of top results to retrieve from the Vector Store.
     */
    private final Integer topK;

    /**
     * The service used to interact with Google Drive.
     */
    private final GoogleDriveService googleDriveService;

    private final DocumentMetadataRepository documentMetadataRepository;

    public GoogleDriveRetriever(
            final @NonNull Integer topK,
            final @NonNull Boolean isProcessingEnabled,
            final @NonNull GoogleDriveService googleDriveService,
            DocumentMetadataRepository documentMetadataRepository) {

        this.googleDriveService = googleDriveService;

        if (topK <= 0) {
            throw new IllegalArgumentException("TopK must be a positive number.");
        }
        this.topK = topK;
        this.documentMetadataRepository = documentMetadataRepository;

        if (isProcessingEnabled) {
            processGoogleDriveStorage();
            log.info("Starting Google Drive retriever...");
        }
    }

    @Override
    public RagContext retrieve(final RagRequest request) {
        return RagContext.builder().knowledge(Collections.emptyList()).build();
    }

    /**
     * Lists all regular files in the local directory, processes each file to create chunks, and then adds these chunks
     * to the vector store.
     *
     * @throws RuntimeException if an I/O error occurs when processing file storage folder.
     */
    private void processGoogleDriveStorage() {
        try {
            var documents = googleDriveService.processFiles();
            documentMetadataRepository.saveAll(documents);
        } catch (UnsupportedExtensionException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
