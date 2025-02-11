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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.lang.NonNull;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentContentDTO;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentMetadataDTO;
import ai.yda.framework.rag.retriever.google_drive.service.processor.*;

public class DocumentProcessorProvider {
    private final ExcelDocumentProcessor excelProcessor;
    private final TikaDocumentProcessor tikaProcessor;
    private final ImageDocumentProcessor imageProcessor;

    public DocumentProcessorProvider(
            final @NonNull ExcelDocumentProcessor excelProcessor,
            final @NonNull TikaDocumentProcessor tikaProcessor,
            final @NonNull ImageDocumentProcessor imageProcessor) {
        this.excelProcessor = excelProcessor;
        this.tikaProcessor = tikaProcessor;
        this.imageProcessor = imageProcessor;
    }

    public List<DocumentContentDTO> processDocument(
            final String extension, final InputStream inputStream, final DocumentMetadataDTO metadataDTO)
            throws IOException {
        return getProcessor(extension).processDocument(inputStream, metadataDTO);
    }

    private DocumentProcessor getProcessor(final String extension) {
        var documentType = DocumentType.fromExtension(extension);
        return switch (documentType) {
            case EXCEL -> excelProcessor;
            case PDF, WORD, POWERPOINT, HTML -> tikaProcessor;
            case PNG, JPEG -> imageProcessor;
        };
    }
}
