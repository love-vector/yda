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
package ai.yda.framework.rag.retriever.website.retriver;

import ai.yda.framework.rag.core.model.Chunk;
import ai.yda.framework.rag.core.model.CrawlResult;
import ai.yda.framework.rag.core.retriever.*;
import ai.yda.framework.rag.retriever.website.services.chunking.SlidingWindowChunking;
import ai.yda.framework.rag.retriever.website.services.crawling.WebsiteService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.lang.NonNull;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Retrieves website Context data from a Vector Store based on a User Request. It processes website sitemap and uses a
 * Vector Store to perform similarity searches. If website processing is enabled, it processes website urls during
 * initialization.
 *
 * @author Iryna Kopchak
 * @author Bogdan Synenko
 * @see WebsiteService
 * @see VectorStore
 * @since 0.1.0
 */
@Slf4j
public class WebsiteRetriever implements Retriever<RagRequest, RagContext>, Indexer {
    /**
     * The Vector Store used to retrieve Context data for user Request through similarity search.
     */
    private final VectorStore vectorStore;

    /**
     * The website's sitemap url.
     */
    private final String sitemapUrl;

    /**
     * The number of top results to retrieve from the Vector Store.
     */
    private final Integer topK;

    private final WebsiteService websiteService = new WebsiteService();

    /**
     * Constructs a new {@link WebsiteRetriever} instance with the specified vectorStore, sitemapUrl, topK and
     * isProcessingEnabled parameters.
     *
     * @param vectorStore         the {@link VectorStore} instance used for storing and retrieving vector data.
     *                            This parameter cannot be {@code null} and is used to interact with the Vector Store.
     * @param sitemapUrl          the website's sitemap url. This parameter cannot be {@code null} and is used to
     *                            process and store data to the Vector Store.
     * @param topK                the number of top results to retrieve from the Vector Store. This value must be a
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
            process(null);
        }
    }

    @Override
    public List<Document> index(final List<CrawlResult> list) {
        var chunks = process(list);
        var documents = chunks.stream()
                .map(chunk -> new Document(chunk.getText(), Map.of("url", chunk.getUrl(), "chunkIndex", String.valueOf(chunk.getIndex()))))
                .collect(Collectors.toList());
        save(documents);
        return documents;
    }

    @Override
    public List<Chunk> process(List<CrawlResult> chunks) {
        List<Chunk> result = new ArrayList<>();
        ChunkStrategy chunkStrategy = new SlidingWindowChunking(10, 1);
        int chunkIndex = 0;
        for (CrawlResult crawlResult : chunks) {
            var chunkedTexts = chunkStrategy.splitChunks(crawlResult.getContent());
            for (String chunkText : chunkedTexts) {
                var chunk = new Chunk(chunkText, chunkIndex++, crawlResult.getLink());
                result.add(chunk);
            }
        }
        return result;
    }

    @Override
    public void save(List<Document> documents) {
        vectorStore.add(documents);
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
