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
package ai.yda.framework.rag.retriever.google_drive.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentContentEntity;
import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;

@Component
@RequiredArgsConstructor
public class DocumentProcessorProvider {
    private final ExelDocumentProcessor excelProcessor;
    private final TikaDocumentProcessor tikaProcessor;

    public List<DocumentContentEntity> processDocument(
            final String extension, final InputStream inputStream, final DocumentMetadataEntity documentMetadata)
            throws IOException {
        var processor = getProcessor(extension);
        return processor.processDocument(inputStream, documentMetadata);
    }

    private DocumentProcessor getProcessor(final String extension) {
        var documentType = DocumentType.fromExtension(extension);
        return switch (documentType) {
            case EXCEL -> excelProcessor;
            case PDF, WORD, POWERPOINT, HTML -> tikaProcessor;
        };
    }
}
