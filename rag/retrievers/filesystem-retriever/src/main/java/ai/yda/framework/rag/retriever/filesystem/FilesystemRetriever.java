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
package ai.yda.framework.rag.retriever.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import ai.yda.framework.rag.core.retriever.chunking.model.DocumentData;
import ai.yda.framework.rag.core.retriever.chunking.factory.ChunkingAlgorithm;
import ai.yda.framework.rag.core.retriever.chunking.factory.PatternBasedChunking;
import ai.yda.framework.rag.retriever.filesystem.exception.FileReadException;
import ai.yda.framework.rag.retriever.filesystem.service.FilesystemService;


/**
 * Retrieves filesystem Context data from a Vector Store based on a Request. It processes files stored in a specified
 * directory and uses a Vector Store to perform similarity searches. If file processing is enabled, it processes files
 * in the storage folder during initialization.
 *
 * @author Bogdan Synenko
 * @author Dmitry Marchuk
 * @author Iryna Kopchak
 * @see FilesystemService
 * @see VectorStore
 * @since 0.2.0
 */
@Slf4j
public class FilesystemRetriever extends Indexer<DocumentData> implements Retriever<RagRequest, RagContext> {
    /**
     * The Vector Store used to retrieve Context data for User Request through similarity search.
     */
    private final VectorStore vectorStore;

    /**
     * The path to the directory where files are stored.
     */
    private final Path fileStoragePath;

    /**
     * The algorithm used for chunking the content of the files.
     */
    private final ChunkingAlgorithm chunkingAlgorithm;

    /**
     * The number of top results to retrieve from the Vector Store.
     */
    private final Integer topK;

    private final FilesystemService filesystemService = new FilesystemService();

    /**
     * Constructs a new {@link FilesystemRetriever} instance with the specified vectorStore, fileStoragePath, topK and
     * isIndexingEnabled parameters.
     *
     * @param vectorStore       the {@link VectorStore} instance used for storing and retrieving vector data.
     *                          This parameter cannot be {@code null} and is used to interact with the Vector Store.
     * @param fileStoragePath   the path to the directory where files are stored. This parameter cannot be
     *                          {@code null} and is used to process and store files to the Vector Store.
     * @param topK              the number of top results to retrieve from the Vector Store. This value must be a
     *                          positive integer.
     * @param isIndexingEnabled a {@link Boolean} flag indicating whether file processing should be enabled during
     *                          initialization. If {@code true}, the method {@link #index()} will
     *                          be called to process the files in the specified storage path.
     * @param chunkingAlgorithm the algorithm used to split document content into chunks for further processing.
     * @throws IllegalArgumentException if {@code topK} is not a positive number.
     */
    public FilesystemRetriever(
            final @NonNull VectorStore vectorStore,
            final @NonNull String fileStoragePath,
            final @NonNull Integer topK,
            final @NonNull Boolean isIndexingEnabled,
            final @NonNull ChunkingAlgorithm chunkingAlgorithm) {
        if (topK <= 0) {
            throw new IllegalArgumentException("TopK must be a positive number.");
        }
        this.vectorStore = vectorStore;
        this.fileStoragePath = Paths.get(fileStoragePath);
        this.topK = topK;
        this.chunkingAlgorithm = chunkingAlgorithm;

        if (Boolean.TRUE.equals(isIndexingEnabled)) {
            index();
        }
    }

    /**
     * Retrieves Context data based on the given Request by performing a similarity search in the Vector Store.
     *
     * @param request the {@link RagRequest} object containing the User query for the similarity search.
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

    /**
     * Processes a list of {@link DocumentData} objects by splitting their content into chunks based on the chosen
     * {@link ChunkingAlgorithm}.
     *
     * @return a list of processed {@link DocumentData} objects, each representing a chunk of a document.
     */
    @Override
    protected List<DocumentData> process() {
        try (var paths = Files.list(fileStoragePath)) {
            var fileList = paths.filter(Files::isRegularFile).toList();

            if (fileList.isEmpty()) {
                log.debug("No files to process in directory: {}", fileStoragePath);
                return null;
            }

            var processedFilesList = filesystemService.createDocumentDataFromFiles(fileList);
            moveFilesToProcessedFolder(fileList);
            PatternBasedChunking patternBasedChunking = new PatternBasedChunking();

            return patternBasedChunking.chunkList(chunkingAlgorithm, processedFilesList).stream()
                    .map(chunk -> new DocumentData(
                            chunk.getText(),
                            Map.of("documentId", chunk.getDocumentId(), "chunkIndex", String.valueOf(chunk.getIndex()))))
                    .toList();
        } catch (IOException e) {
            log.error("Error processing files in directory {}: {}", fileStoragePath, e.getMessage());
            throw new FileReadException(e);
        }
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
    protected void save(final List<DocumentData> documentDataList) {
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
     * Moves all files from the local directory to the "processed" folder.
     *
     * @throws IOException if an I/O error occurs when accessing or moving the files.
     */
    private void moveFilesToProcessedFolder(final List<Path> fileList) throws IOException {
        var processedDir = fileStoragePath.resolveSibling("processed");

        if (!Files.exists(processedDir)) {
            Files.createDirectory(processedDir);
        }

        fileList.parallelStream().forEach(file -> {
            try {
                Files.move(file, processedDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                log.error("Failed to move file {} to processed directory: {}", file, e);
            }
        });
    }
}
