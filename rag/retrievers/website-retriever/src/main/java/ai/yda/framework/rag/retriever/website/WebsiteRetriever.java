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
package ai.yda.framework.rag.retriever.website;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.lang.NonNull;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.retriever.website.service.WebsiteService;

/**
 * Retrieves website context data from a vector store based on a query. It processes website sitemap and uses a vector
 * store to perform similarity searches. If website processing is enabled, it processes website urls during
 * initialization.
 *
 * @author Bogdan Synenko
 * @author Iryna Kopchak
 * @see WebsiteService
 * @see VectorStore
 * @since 0.1.0
 */
@Slf4j
public class WebsiteRetriever implements Retriever<RagRequest, RagContext> {
    /**
     * The vector store used to retrieve context data for user request through similarity search.
     */
    private final VectorStore vectorStore;

    /**
     * The website's sitemap url.
     */
    private final String sitemapUrl;

    /**
     * The number of top results to retrieve from the vector store.
     */
    private final Integer topK;

    private final WebsiteService websiteService = new WebsiteService();

    /**
     * Constructs a new {@link WebsiteRetriever} instance with the specified vectorStore, sitemapUrl, topK and
     * isProcessingEnabled parameters.
     *
     * @param vectorStore         the {@link VectorStore} instance used for storing and retrieving vector data.
     *                            This parameter cannot be {@code null} and is used to interact with the vector store.
     * @param sitemapUrl          the website's sitemap url. This parameter cannot be {@code null} and is used to
     *                            process and store data to the vector store.
     * @param topK                the number of top results to retrieve from the vector store. This value must be a
     *                            positive integer.
     * @param isProcessingEnabled a {@link Boolean} flag indicating whether website processing should be enabled during
     *                            initialization. If {@code true}, the method {@link #processWebsite()} will
     *                            be called to process the files in the specified storage path.
     * @throws IllegalArgumentException if {@code topK} is not a positive number.
     */
    public WebsiteRetriever(
            final @NonNull VectorStore vectorStore,
            final @NonNull String sitemapUrl,
            final @NonNull Integer topK,
            final @NonNull Boolean isProcessingEnabled) {
        if (topK <= 0) {
            throw new IllegalArgumentException("TopK must be a positive number.");
        }
        this.vectorStore = vectorStore;
        this.sitemapUrl = sitemapUrl;
        this.topK = topK;

        if (isProcessingEnabled) {
            processWebsite();
        }
    }

    /**
     * Retrieves context data based on the given request by performing a similarity search in the vector store.
     *
     * @param request the request object containing the user query for the similarity search.
     * @return a {@link RagContext} object containing the knowledge obtained from the similarity search.
     */
    @Override
    public RagContext retrieve(final RagRequest request) {
        return RagContext.builder()
                .knowledge(
                        vectorStore
                                .similaritySearch(
                                        SearchRequest.query(request.getQuery()).withTopK(topK))
                                .parallelStream()
                                .map(Document::getContent)
                                .toList())
                .build();
    }

    /**
     * Processes all website urls by creating document chunks and adding them to the vector store.
     */
    private void processWebsite() {
        var pageDocuments = websiteService.createChunkDocumentsFromSitemapUrl(sitemapUrl);
        vectorStore.add(pageDocuments);
    }
}
