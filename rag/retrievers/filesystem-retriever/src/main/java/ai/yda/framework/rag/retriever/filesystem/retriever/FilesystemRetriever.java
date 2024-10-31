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
package ai.yda.framework.rag.retriever.filesystem.retriever;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.lang.NonNull;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.retriever.filesystem.extractor.service.FilesystemService;

/**
 * Retrieves filesystem Context data from a Vector Store based on a Request. It processes files stored in a specified
 * directory and uses a Vector Store to perform similarity searches. If file processing is enabled, it processes files
 * in the storage folder during initialization.
 *
 * @author Dmitry Marchuk
 * @author Iryna Kopchak
 * @see FilesystemService
 * @see VectorStore
 * @since 0.1.0
 */
@Slf4j
public class FilesystemRetriever implements Retriever<RagRequest, RagContext> {
    private final VectorStore vectorStore;
    private final Integer topK;

    public FilesystemRetriever(final @NonNull VectorStore vectorStore, final @NonNull Integer topK) {
        if (topK <= 0) {
            throw new IllegalArgumentException("TopK must be a positive number.");
        }
        this.vectorStore = vectorStore;
        this.topK = topK;
    }

    /**
     * Retrieves Context data based on the given Request by performing a similarity search in the Vector Store.
     *
     * @param request the {@link RagRequest} object containing the User query for the similarity search.
     * @return a {@link RagContext} object containing the Knowledge obtained from the similarity search.
     */
    @Override
    public List<RagContext> retrieve(final RagRequest request) {
        var documentList = vectorStore.similaritySearch(
                SearchRequest.query(request.getQuery()).withTopK(topK));
        return documentList.stream()
                .map(document -> new RagContext(document.getContent(), document.getMetadata()))
                .collect(Collectors.toList());
    }
}
