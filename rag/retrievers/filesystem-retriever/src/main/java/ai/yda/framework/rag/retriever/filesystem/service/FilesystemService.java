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

@Slf4j
public class FilesystemService {
    private static final int CHUNK_MAX_LENGTH = 1000;

    public List<Document> createChunkDocumentsFromFiles(final List<Path> filePathList) {
        return filePathList.parallelStream()
                .map(this::splitFileIntoChunkDocuments)
                .flatMap(List::stream)
                .toList();
    }

    private List<Document> splitFileIntoChunkDocuments(final Path filePath) {
        var pdfContent = FileUtil.readPdf(filePath.toFile());
        var fileName = filePath.getFileName();
        log.debug("Processing file: {}", fileName);
        var preprocessedContent = ContentUtil.preprocessContent(pdfContent);
        return ContentUtil.splitContent(preprocessedContent, CHUNK_MAX_LENGTH).parallelStream()
                .map(documentChunk -> new Document(documentChunk, Map.of("fileName", fileName)))
                .toList();
    }
}
