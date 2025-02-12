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
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.NonNull;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentContentIdDTO;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentIdsDTO;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentContentPort;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentMetadataPort;
import ai.yda.framework.rag.retriever.google_drive.service.GoogleDriveService;

/**
 * The {@code GoogleDriveRetriever} class is a Retriever implementation that interacts with Google Drive
 * to retrieve contextual data for a given {@link Query}. It uses the {@link GoogleDriveService}
 * to perform operations on Google Drive.
 *
 * <p>This retriever is designed to process and fetch documents or data stored in Google Drive
 * based on the provided configuration and request context.
 *
 * <p>Usage:
 * - Instantiate this class with the desired `topK` (number of results to retrieve) and a
 * configured {@link GoogleDriveService}.
 * - Use the {@link #retrieve(Query)} method to perform the data retrieval operation.
 *
 * <p>Dependencies:
 * - Requires a properly configured {@link GoogleDriveService} instance.
 *
 * @author dmmrch
 * @since 0.2.0
 */
@Slf4j
public class GoogleDriveRetriever implements DocumentRetriever {

    private static final String RETRIEVAL_SYSTEM_INSTRUCTION =
            """
                    1. Definition
                    You are an assistant tasked with selecting the documents chunks id's most relevant to the user's query.
                    Strictly adhere to the instruction by providing accurate, concise responses based solely on the existing document chunks and their IDs, with no fabrication.

                    2. Operational Guidelines
                    Follow these steps sequentially and do not skip or combine steps:
                    2.1 Obtain all files along with their AI description.
                    2.2 Select ONLY IDs of files with ai description relevant to the user's query. If no suitable files are found, return an empty list [].
                    2.3 Using the IDs of the files deemed relevant, retrieve the corresponding document chunks.
                    2.4 Evaluate the relevance of each chunk based on its content and the user's query. Strictly match the query context to the chunk's content and exclude loosely related chunks.
                    2.5 Identify IDs of chunks that are most relevant to the user's query. If no suitable chunks are found, return an empty list [].

                    3. Response Format.
                    Construct a JSON array of objects. Each object must represent a relevant content chunk and adhere to the following structure:
                    [
                        {
                        "contentId":"string"
                        }
                    ]
                    Deduplicate chunk IDs to ensure that no chunk appears more than once in the response. If duplicates are found, keep only one instance of each unique ID.
                    Ensure the output strictly complies with the specified format.""";

    /**
     * The service used to interact with Google Drive.
     */
    private final GoogleDriveService googleDriveService;

    private final ChatClient chatClient;

    private final DocumentMetadataPort documentMetadataPort;
    private final DocumentContentPort documentContentPort;

    public GoogleDriveRetriever(
            final @NonNull Boolean isProcessingEnabled,
            final @NonNull ChatClient chatClient,
            final @NonNull DocumentMetadataPort documentMetadataPort,
            final @NonNull DocumentContentPort documentContentPort,
            final @NonNull GoogleDriveService googleDriveService)
            throws IOException {
        this.chatClient = chatClient;
        this.documentMetadataPort = documentMetadataPort;
        this.documentContentPort = documentContentPort;

        this.googleDriveService = googleDriveService;

        if (isProcessingEnabled) {
            log.info("Starting Google Drive retriever...");
            googleDriveService.syncDriveAndProcessDocuments();
        }
    }

    @Override
    public @NonNull List<Document> retrieve(@NonNull final Query query) {
        var getAllDocumentsFunction = FunctionCallback.builder()
                .function("getAllDocuments", documentMetadataPort::getAllFilesAiDescription)
                .description("Retrieves a list of all documents along with their descriptions")
                .build();
        var getDocumentChunksFunction = FunctionCallback.builder()
                .function("getFilesChunks", documentContentPort::getDocumentsContents)
                .description("Fetch chunks content of documents using a provided list of document IDs")
                .inputType(DocumentIdsDTO.class)
                .build();

        var documentContentIds = chatClient
                .prompt()
                .user(query.text())
                .system(RETRIEVAL_SYSTEM_INSTRUCTION)
                .functions(getAllDocumentsFunction, getDocumentChunksFunction)
                .call()
                .entity(new ParameterizedTypeReference<List<DocumentContentIdDTO>>() {});

        if (log.isDebugEnabled() && documentContentIds != null) {
            log.debug(
                    "Retrieved documents with IDs: {}",
                    documentContentIds.stream()
                            .map(DocumentContentIdDTO::contentId)
                            .toList());
        }

        return documentContentPort.getDocumentsByIds(documentContentIds);
    }
}
