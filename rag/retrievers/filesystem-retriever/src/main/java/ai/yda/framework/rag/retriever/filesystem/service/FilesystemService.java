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
package ai.yda.framework.rag.retriever.filesystem.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;

import ai.yda.framework.rag.core.util.ContentUtil;
import ai.yda.framework.rag.retriever.filesystem.util.FileUtil;

/**
 * Provides methods to process files from the filesystem, specifically for creating chunked documents from files.
 * This class handles the reading, preprocessing, and splitting of files into smaller chunks, which are then
 * converted into {@link Document} objects. It is used to manage and transform file contents to facilitate further
 * processing or retrieval.
 *
 * @author Iryna Kopchak
 * @author Dmitry Marchuk
 * @see FileUtil
 * @see ContentUtil
 * @since 0.1.0
 */
@Slf4j
public class FilesystemService {

    /**
     * The maximum length of a chunk in characters.
     */
    private static final int CHUNK_MAX_LENGTH = 1000;

    /**
     * Default constructor for {@link FilesystemService}.
     */
    public FilesystemService() {}

    /**
     * Processes a list of file paths, reads each file, preprocesses the content, splits it into chunks, and then
     * converts each chunk into a {@link Document} object.
     *
     * @param filePathList a list of {@link Path} to the files to be processed.
     * @return a list of {@link Document} objects created from the chunks of files.
     */
    public List<Document> createChunkDocumentsFromFiles(final List<Path> filePathList) {
        return filePathList.parallelStream()
                .map(this::splitFileIntoChunkDocuments)
                .flatMap(List::stream)
                .toList();
    }

    /**
     * Preprocesses and split of each file into chunks of a maximum length defined by {@link #CHUNK_MAX_LENGTH}. The
     * method reads the content of a PDF file, preprocesses it to clean and format the text, and then splits the
     * preprocessed content into smaller chunks based on the maximum length. Each chunk is converted into a
     * {@link Document} object with associated metadata.
     *
     * @param filePath the {@link Path} of the file to be processed.
     * @return a list of {@link Document} objects created from the chunks of the file.
     */
    private List<Document> splitFileIntoChunkDocuments(final Path filePath) {
        var pdfContent = FileUtil.readPdf(filePath.toFile());
        var fileName = filePath.getFileName();
        log.debug("Processing file: {}", fileName);
        return ContentUtil.preprocessAndSplitContent(pdfContent, CHUNK_MAX_LENGTH).parallelStream()
                .map(documentChunk -> new Document(documentChunk, Map.of("fileName", fileName)))
                .toList();
    }
}
