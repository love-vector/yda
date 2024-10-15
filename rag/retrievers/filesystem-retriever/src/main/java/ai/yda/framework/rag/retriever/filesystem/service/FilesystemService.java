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

import ai.yda.framework.rag.core.retriever.chunking.model.DocumentData;
import ai.yda.framework.rag.core.util.ContentUtil;
import ai.yda.framework.rag.retriever.filesystem.util.FileUtil;

/**
 * Provides methods to process files from the filesystem, specifically for creating chunked documents from files.
 * This class handles the reading, preprocessing which are then
 * converted into {@link DocumentData} objects. It is used to manage and transform file contents to facilitate further
 * processing or retrieval.
 *
 * @author Bogdan Synenko
 * @author Iryna Kopchak
 * @author Dmitry Marchuk
 * @see FileUtil
 * @see ContentUtil
 * @since 0.1.0
 */
@Slf4j
public class FilesystemService {

    /**
     * Default constructor for {@link FilesystemService}.
     */
    public FilesystemService() {}

    /**
     * Creates a list of {@link DocumentData} objects from the provided list of file paths.
     * Each file is read, preprocessed, and split into chunks.
     *
     * @param filePathList the list of file paths to be processed.
     * @return a list of {@link DocumentData} objects representing the chunks of each file.
     */
    public List<DocumentData> createDocumentDataFromFiles(final List<Path> filePathList) {
        return filePathList.parallelStream()
                .map(this::splitFileIntoDocumentData)
                .toList();
    }

    /**
     * method reads the content of a PDF file, preprocesses it to clean and format the text, and then splits the
     * {@link DocumentData} object with associated metadata.
     *
     * @param filePath the {@link Path} of the file to be processed.
     * @return a list of {@link DocumentData} objects created from the chunks of the file.
     */
    public DocumentData splitFileIntoDocumentData(final Path filePath) {
        var pdfContent = FileUtil.readPdf(filePath.toFile());
        var fileName = filePath.getFileName();
        log.debug("Processing file: {}", fileName);
        ContentUtil.preprocessContent(pdfContent);
        return new DocumentData(pdfContent, Map.of("documentId", fileName));
    }
}
