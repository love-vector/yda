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

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.lang.NonNull;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.retriever.filesystem.service.FilesystemService;

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
    /**
     * The Vector Store used to retrieve Context data for User Request through similarity search.
     */
    private final VectorStore vectorStore;

    /**
     * The path to the directory where files are stored.
     */
    private final Path fileStoragePath;

    /**
     * The number of top results to retrieve from the Vector Store.
     */
    private final Integer topK;

    private final FilesystemService filesystemService = new FilesystemService();

    /**
     * Constructs a new {@link FilesystemRetriever} instance with the specified vectorStore, fileStoragePath, topK and
     * isProcessingEnabled parameters.
     *
     * @param vectorStore         the {@link VectorStore} instance used for storing and retrieving vector data.
     *                            This parameter cannot be {@code null} and is used to interact with the Vector Store.
     * @param fileStoragePath     the path to the directory where files are stored. This parameter cannot be
     *                            {@code null} and is used to process and store files to the Vector Store.
     * @param topK                the number of top results to retrieve from the Vector Store. This value must be a
     *                            positive integer.
     * @param isProcessingEnabled a {@link Boolean} flag indicating whether file processing should be enabled during
     *                            initialization. If {@code true}, the method {@link #processFileStorageFolder()} will
     *                            be called to process the files in the specified storage path.
     * @throws IllegalArgumentException if {@code topK} is not a positive number.
     */
    public FilesystemRetriever(
            final @NonNull VectorStore vectorStore,
            final @NonNull String fileStoragePath,
            final @NonNull Integer topK,
            final @NonNull Boolean isProcessingEnabled) {
        if (topK <= 0) {
            throw new IllegalArgumentException("TopK must be a positive number.");
        }
        this.vectorStore = vectorStore;
        this.fileStoragePath = Paths.get(fileStoragePath);
        this.topK = topK;

        if (isProcessingEnabled) {
            processFileStorageFolder();
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
     * Lists all regular files in the local directory, processes each file to create chunks, and then adds these chunks
     * to the vector store.
     *
     * @throws RuntimeException if an I/O error occurs when processing file storage folder.
     */
    private void processFileStorageFolder() {
        try (var paths = Files.list(fileStoragePath)) {
            var fileList = paths.filter(Files::isRegularFile).toList();

            if (fileList.isEmpty()) {
                log.debug("No files to process in directory: {}", fileStoragePath);
                return;
            }

            var documents = filesystemService.createChunkDocumentsFromFiles(fileList);
            vectorStore.add(documents);
            moveFilesToProcessedFolder(fileList);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
