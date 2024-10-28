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
package ai.yda.framework.rag.retriever.filesystem.extractor.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.retriever.DataExtractor;
import ai.yda.framework.rag.core.util.ContentUtil;
import ai.yda.framework.rag.retriever.filesystem.extractor.util.FileUtil;

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
public class FilesystemService implements DataExtractor<List<DocumentData>> {

    public FilesystemService() {}

    @Override
    public List<DocumentData> extract(String source) {
        Path fileStoragePath = Paths.get(source);
        try {
            var fileList = listRegularFiles(fileStoragePath);
            if (fileList.isEmpty()) {
                log.debug("No files to process in directory: {}", fileStoragePath);
                return List.of();
            }

            var documents = createDocumentDataFromFiles(fileList);
            moveFilesToProcessedFolder(fileList, fileStoragePath);

            return documents;
        } catch (IOException e) {
            throw new RuntimeException("Error processing files in storage folder", e);
        }
    }

    private List<Path> listRegularFiles(Path fileStoragePath) throws IOException {
        try (var paths = Files.list(fileStoragePath)) {
            return paths.filter(Files::isRegularFile).toList();
        }
    }

    private List<DocumentData> createDocumentDataFromFiles(final List<Path> filePathList) {
        return filePathList.parallelStream()
                .map(this::splitFileIntoDocumentData)
                .toList();
    }

    private DocumentData splitFileIntoDocumentData(final Path filePath) {
        var pdfContent = FileUtil.readPdf(filePath.toFile());
        var fileName = filePath.getFileName();
        log.debug("Processing file: {}", fileName);
        ContentUtil.preprocessContent(pdfContent);
        return new DocumentData(pdfContent, Map.of("documentId", fileName));
    }

    private void moveFilesToProcessedFolder(final List<Path> fileList, Path fileStoragePath) throws IOException {
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
