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
package ai.yda.framework.rag.retriever.google_drive.service;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentContentDTO;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentMetadataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class DocumentChunkingService {
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("pdf", "docx", "ppt", "pptx", "html");
    private final TokenTextSplitter tokenTextSplitter;

    public DocumentChunkingService(TokenTextSplitter tokenTextSplitter) {
        this.tokenTextSplitter = tokenTextSplitter;
    }

    public List<String> chunkList(final String documentContent) {
        return tokenTextSplitter.split(new Document(documentContent)).stream()
                .map(Document::getText)
                .collect(Collectors.toList());
    }

    public DocumentMetadataDTO processContent(
            final DocumentMetadataDTO documentMetadataDTO, final String filesExtension) {
        if (!isSupportedFileType(filesExtension)) {
            log.warn("Unsupported file extension: {}", filesExtension);
            return documentMetadataDTO;
        }

        var documentContentDTO = documentMetadataDTO.getDocumentContents().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Document contents are empty for document: " + documentMetadataDTO.getDocumentId()));

        var documentForSplitting = documentContentDTO.getChunkContent();

        log.debug("Processing document: {}", documentMetadataDTO.getName());

        var splitOntoChunks = chunkList(documentForSplitting);
        var transformEveryChunks = splitOntoChunks.stream()
                .map(chunk -> DocumentContentDTO.builder()
                        .documentMetadataId(documentMetadataDTO.getDocumentId())
                        .chunkName(documentMetadataDTO.getName())
                        .contentId(documentContentDTO.getContentId())
                        .chunkContent(chunk)
                        .build())
                .toList();
        documentMetadataDTO.setDocumentContents(transformEveryChunks);

        log.debug("Processed document: {} ({} chunks)", documentMetadataDTO.getName(), transformEveryChunks.size());

        return documentMetadataDTO;
    }

    private boolean isSupportedFileType(final String fileExtension) {
        return SUPPORTED_EXTENSIONS.contains(fileExtension.toLowerCase());
    }
}
