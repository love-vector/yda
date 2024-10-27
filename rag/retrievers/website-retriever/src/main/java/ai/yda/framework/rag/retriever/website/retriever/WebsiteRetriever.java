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
package ai.yda.framework.rag.retriever.website.retriever;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.Retriever;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.lang.NonNull;

/**
 * Retrieves website Context data from a Vector Store based on a User Request. It processes website or sitemap and uses
 * a Vector Store to perform similarity searches. If website processing is enabled, it processes website urls during
 * initialization.
 *
 * @author Iryna Kopchak
 * @author Bogdan Synenko
 * @see VectorStore
 * @since 0.1.0
 */
@Slf4j
public class WebsiteRetriever implements Retriever<RagRequest, RagContext> {

    /**
     * The Vector Store used to retrieve Context data for user Request through similarity search.
     */
    private final VectorStore vectorStore;

    /**
     * The number of top results to retrieve from the Vector Store.
     */
    private final Integer topK;

    /**
     * Constructs a new {@link WebsiteRetriever} instance with the specified vectorStore, url, topK and
     * isProcessingEnabled parameters.
     *
     * @param vectorStore the {@link VectorStore} instance used for storing and retrieving vector data.
     *                    This parameter cannot be {@code null} and is used to interact with the Vector Store.
     *                    process and store data to the Vector Store.
     * @param topK        the number of top results to retrieve from the Vector Store. This value must be a
     *                    positive integer.
     *                    be called to process the files in the specified storage path.
     * @throws IllegalArgumentException if {@code topK} is not a positive number.
     */
    public WebsiteRetriever(
            final @NonNull VectorStore vectorStore,
            final @NonNull Integer topK) {
        if (topK <= 0) {
            throw new IllegalArgumentException("TopK must be a positive number.");
        }
        this.vectorStore = vectorStore;
        this.topK = topK;
    }


    /**
     * Retrieves Context data based on the given Request by performing a similarity search in the Vector Store.
     *
     * @param request the Request object containing the User query for the similarity search.
     * @return a {@link RagContext} object containing the Knowledge obtained from the similarity search.
     */
    @Override
    public RagContext retrieve(final RagRequest request) {
        return RagContext.builder()
                .knowledge(
                        vectorStore
                                .similaritySearch(
                                        SearchRequest.query(request.getQuery()).withTopK(topK))
                                .parallelStream()
                                .map(document -> {
                                    log.debug("Document metadata: {}", document.getMetadata());
                                    return document.getContent();
                                })
                                .toList())
                .build();

    }
}