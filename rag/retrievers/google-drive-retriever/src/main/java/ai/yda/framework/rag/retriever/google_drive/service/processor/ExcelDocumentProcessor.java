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
package ai.yda.framework.rag.retriever.google_drive.service.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.lang.NonNull;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentContentDTO;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentMetadataDTO;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentContentMapper;
import ai.yda.framework.rag.retriever.google_drive.service.SplitSheetBodyContentHandler;
import ai.yda.framework.rag.retriever.google_drive.service.TikaExcelDocumentReader;

import static ai.yda.framework.rag.retriever.google_drive.util.Constant.DOCUMENT_METADATA_NAME;

public class ExcelDocumentProcessor implements DocumentProcessor {

    private final DocumentContentMapper documentContentMapper;

    public ExcelDocumentProcessor(final @NonNull DocumentContentMapper documentContentMapper) {
        this.documentContentMapper = documentContentMapper;
    }

    @Override
    public List<DocumentContentDTO> processDocument(
            final InputStream inputStream, final DocumentMetadataDTO metadataDTO) throws IOException {
        return new TikaExcelDocumentReader(
                        new ByteArrayResource(inputStream.readAllBytes()),
                        new SplitSheetBodyContentHandler(),
                        ExtractedTextFormatter.defaults())
                .get().stream()
                        .map(sheet -> documentContentMapper.toDTO(
                                sheet.getMetadata().get(DOCUMENT_METADATA_NAME).toString(),
                                sheet.getText(),
                                metadataDTO.getDocumentId()))
                        .toList();
    }
}
