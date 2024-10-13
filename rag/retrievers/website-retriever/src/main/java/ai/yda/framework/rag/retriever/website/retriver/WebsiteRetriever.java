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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.lang.NonNull;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.Indexer;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.retriever.chunking.entity.DocumentData;
import ai.yda.framework.rag.core.retriever.chunking.factory.ChunkingAlgorithm;
import ai.yda.framework.rag.core.retriever.chunking.factory.PatternBasedChunking;
import ai.yda.framework.rag.retriever.website.extractor.WebExtractor;

/**
 * Retrieves website Context data from a Vector Store based on a User Request.
 * This class crawls or extracts data from a specified website or sitemap URL,
 * chunks the content using a provided chunking algorithm, and then stores the chunks
 * in a Vector Store. It supports both retrieval and indexing functionalities.
 *
 * <p>If processing is enabled, it will also process the website content at initialization,
 * chunk the data, and store the results in the Vector Store.</p>
 *
 * @author Iryna Kopchak
 * @author Bogdan Synenko
 * @author Nikita Litvinov
 * @see VectorStore
 * @see WebExtractor
 * @since 0.2.0
 */
@Slf4j
public class WebsiteRetriever implements Retriever<RagRequest, RagContext>, Indexer<DocumentData> {
    /**
     * The Vector Store used to retrieve Context data for user Request through similarity search.
     */
    private final VectorStore vectorStore;

    /**
     * The website or sitemap url.
     */
    private final String url;

    /**
     * The algorithm used for chunking the content of the extracted website data.
     */
    private final ChunkingAlgorithm chunkingAlgorithm;

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
     *                            initialization. If {@code true}, the method {@link #index()} will
     *                            be called to process the files in the specified storage path.
     * @param chunkingAlgorithm   the algorithm used to split document content into chunks for further processing.
     * @throws IllegalArgumentException if {@code topK} is not a positive number.
     */
    public WebsiteRetriever(
            final @NonNull WebExtractor webExtractor,
            final @NonNull VectorStore vectorStore,
            final @NonNull String url,
            final @NonNull Integer topK,
            final @NonNull Boolean isProcessingEnabled,
            final @NonNull ChunkingAlgorithm chunkingAlgorithm) {
        if (topK <= 0) {
            throw new IllegalArgumentException("TopK must be a positive number.");
        }
        this.chunkingAlgorithm = chunkingAlgorithm;
        this.webExtractor = webExtractor;
        this.vectorStore = vectorStore;
        this.url = url;
        this.topK = topK;
        if (Boolean.TRUE.equals(isProcessingEnabled)) {
            index();
        }
    }

    /**
     * Indexes the website content by extracting data from the URL, chunking it, and saving it in the Vector Store.
     * <p>This method uses the {@link WebExtractor} to crawl or extract the website content, applies a chunking
     * algorithm to the extracted content, and stores the resulting chunks in the Vector Store.</p>
     */
    @Override
    public void index() {
        List<DocumentData> processedWebsiteList = new ArrayList<>();
        var pageDocuments = webExtractor.extract(url);

        pageDocuments.forEach(crawlResult -> processedWebsiteList.add(
                new DocumentData(crawlResult.getContent(), Map.of("documentId", crawlResult.getUrl()))));
        var documentDataList = process(processedWebsiteList);
        save(documentDataList);
    }

    /**
     * Processes the list of {@link DocumentData} by chunking the content using the selected chunking algorithm.
     * <p>This method applies the chunking algorithm to each document, resulting in smaller content chunks
     * that are easier to manage for further processing or retrieval.</p>
     *
     * @param processedWebsiteList the list of processed website content to be chunked.
     * @return a list of {@link DocumentData} representing the chunked website content.
     */
    @Override
    public List<DocumentData> process(final List<DocumentData> processedWebsiteList) {
        PatternBasedChunking patternBasedChunking = new PatternBasedChunking();
        return patternBasedChunking.chunkList(chunkingAlgorithm, processedWebsiteList).stream()
                .map(chunk -> new DocumentData(
                        chunk.getText(),
                        Map.of("documentId", chunk.getDocumentId(), "chunkIndex", String.valueOf(chunk.getIndex()))))
                .toList();
    }

    /**
     * Saves the processed chunks of website data into the Vector Store.
     * <p>This method processes the list of document data in batches. Each batch contains up to a specified number
     * of documents (currently set to 1000). The method converts each batch of {@link DocumentData} into a list of
     * {@link Document}, and stores the entire batch in the Vector Store at once.</p>
     *
     * <p>This approach improves performance by reducing the overhead associated with storing individual documents,
     * instead processing them in larger groups (batches).</p>
     *
     * @param documentDataList the list of chunked website content to be saved into the Vector Store.
     */
    @Override
    public void save(final List<DocumentData> documentDataList) {
        var batchSize = 1000;
        var totalBatches = (int) Math.ceil((double) documentDataList.size() / batchSize);

        IntStream.range(0, totalBatches).forEach(i -> {
            var fromIndex = i * batchSize;
            var toIndex = Math.min(fromIndex + batchSize, documentDataList.size());
            List<DocumentData> batchList = documentDataList.subList(fromIndex, toIndex);
            List<Document> documents = batchList.stream()
                    .map(documentData -> new Document(documentData.getContent(), documentData.getMetadata()))
                    .toList();

            vectorStore.add(documents);
            log.debug("Processed batch {} of {} with {} documents", i + 1, totalBatches, documents.size());
        });

        log.debug("All information has been processed");
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
