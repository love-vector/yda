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
import java.util.stream.Collectors;

import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.lang.NonNull;

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentContentEntity;
import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentContentMapper;

public class TikaDocumentProcessor implements DocumentProcessor {

    private final DocumentContentMapper documentContentMapper;

    public TikaDocumentProcessor(final @NonNull DocumentContentMapper documentContentMapper) {
        this.documentContentMapper = documentContentMapper;
    }

    // for PDF, DOC/ DOCX, PPT/ PPTX, and HTML.
    @Override
    public List<DocumentContentEntity> processDocument(
            final InputStream inputStream, final DocumentMetadataEntity documentMetadata) throws IOException {
        return new TikaDocumentReader(new ByteArrayResource(inputStream.readAllBytes()))
                .read().stream()
                        .map(document -> documentContentMapper.toEntity(document.getText(), documentMetadata))
                        .collect(Collectors.toList());
    }
}
