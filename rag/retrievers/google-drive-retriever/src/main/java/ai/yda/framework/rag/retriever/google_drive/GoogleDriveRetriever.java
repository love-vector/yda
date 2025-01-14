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
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.NonNull;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentIdDTO;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentMetadataPort;
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

    private static final String RETRIEVAL_SYSTEM_INSTRUCTION =
            """
                    You are an assistant tasked with selecting the documents most relevant to the user's query.
                    Compare the query with document summaries and return a JSON array of objects in the following format:
                    [
                        {
                            "documentId": "string"
                        }
                    ]
                    Each object should represent a relevant document. If no documents are relevant, return an empty
                    array ([]). Ensure the output strictly follows this format.""";

    /**
     * The number of top results to retrieve from the Vector Store.
     */
    private final Integer topK;

    /**
     * The service used to interact with Google Drive.
     */
    private final GoogleDriveService googleDriveService;

    private final ChatClient chatClient;

    private final DocumentMetadataPort documentMetadataPort;

    public GoogleDriveRetriever(
            final @NonNull Integer topK,
            final @NonNull Boolean isProcessingEnabled,
            final @NonNull ChatClient chatClient,
            final @NonNull DocumentMetadataPort documentMetadataPort,
            final @NonNull GoogleDriveService googleDriveService)
            throws IOException {
        this.chatClient = chatClient;
        this.documentMetadataPort = documentMetadataPort;

        this.googleDriveService = googleDriveService;

        if (topK <= 0) {
            throw new IllegalArgumentException("TopK must be a positive number.");
        }
        this.topK = topK;

        if (isProcessingEnabled) {
            log.info("Starting Google Drive retriever...");
            googleDriveService.syncDriveAndProcessDocuments();
        }
    }

    @Override
    public RagContext retrieve(final RagRequest request) {
        var documentIds = chatClient
                .prompt()
                .user(request.getQuery())
                .system(RETRIEVAL_SYSTEM_INSTRUCTION)
                .functions(FunctionCallback.builder()
                        .function("getAllDocuments", documentMetadataPort::getAllFileSummaries)
                        .description("Retrieve all documents to enable precise filtering based on summaries")
                        .build())
                .call()
                .entity(new ParameterizedTypeReference<List<DocumentIdDTO>>() {});

        if (documentIds != null) {
            documentIds.forEach(file -> log.debug("Relevant file id: {}", file.documentId()));
        }

        // TODO: get all relevant chunks
        return RagContext.builder().knowledge(Collections.emptyList()).build();
    }
}
