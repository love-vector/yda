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

import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.lang.NonNull;

import ai.yda.framework.rag.core.util.ContentUtil;
import ai.yda.framework.rag.retriever.website.extractor.WebExtractor;

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
public class WebsiteRetriever implements DocumentRetriever {

    /**
     * The maximum length of a chunk in characters.
     */
    public static final int CHUNK_MAX_LENGTH = 1000;

    /**
     * The Vector Store used to retrieve Context data for user Request through similarity search.
     */
    private final VectorStore vectorStore;

    /**
     * The website or sitemap url.
     */
    private final String url;

    /**
     * The number of top results to retrieve from the Vector Store.
     */
    private final Integer topK;

    /**
     * Extractor used for crawling and extracting web content.
     */
    private final WebExtractor webExtractor;

    /**
     * Constructs a new {@link WebsiteRetriever} instance with the specified vectorStore, url, topK and
     * isProcessingEnabled parameters.
     *
     * @param webExtractor        the extractor used for crawling and extracting web content.
     * @param vectorStore         the {@link VectorStore} instance used for storing and retrieving vector data.
     *                            This parameter cannot be {@code null} and is used to interact with the Vector Store.
     * @param url                 the website or sitemap url. This parameter cannot be {@code null} and is used to
     *                            process and store data to the Vector Store.
     * @param topK                the number of top results to retrieve from the Vector Store. This value must be a
     *                            positive integer.
     * @param isProcessingEnabled a {@link Boolean} flag indicating whether website processing should be enabled during
     *                            initialization. If {@code true}, the method {@link #processUrl()} will
     *                            be called to process the files in the specified storage path.
     * @throws IllegalArgumentException if {@code topK} is not a positive number.
     */
    public WebsiteRetriever(
            final @NonNull WebExtractor webExtractor,
            final @NonNull VectorStore vectorStore,
            final @NonNull String url,
            final @NonNull Integer topK,
            final @NonNull Boolean isProcessingEnabled) {
        if (topK <= 0) {
            throw new IllegalArgumentException("TopK must be a positive number.");
        }
        this.webExtractor = webExtractor;
        this.vectorStore = vectorStore;
        this.url = url;
        this.topK = topK;

        if (isProcessingEnabled) {
            processUrl();
        }
    }

    @Override
    public List<Document> retrieve(Query query) {
        return Objects.requireNonNull(vectorStore.similaritySearch(
                SearchRequest.builder().query(query.text()).topK(topK).build()));
    }

    /**
     * Extracts data from url and processes by creating document chunks and adding them to the Vector Store.
     */
    private void processUrl() {
        var pageDocuments = webExtractor.extract(url).parallelStream()
                .map(result ->
                        ContentUtil.preprocessAndSplitContent(result.getContent(), CHUNK_MAX_LENGTH).parallelStream()
                                .map(chunkContent -> new Document(chunkContent, Map.of("url", result.getUrl())))
                                .toList())
                .flatMap(List::stream)
                .toList();
        vectorStore.add(pageDocuments);
    }
}
