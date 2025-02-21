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
package ai.yda.framework.rag.retriever.google_drive.service.document.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.lang.NonNull;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentContentDTO;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentMetadataDTO;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentContentMapper;

public class TikaDocumentProcessor implements DocumentProcessor {

    private final DocumentContentMapper documentContentMapper;
    private final DocumentTextSplitter documentTextSplitter;

    public TikaDocumentProcessor(
            final @NonNull DocumentContentMapper documentContentMapper,
            final @NonNull DocumentTextSplitter documentTextSplitter) {
        this.documentContentMapper = documentContentMapper;
        this.documentTextSplitter = documentTextSplitter;
    }

    @Override
    public List<DocumentContentDTO> processDocument(
            final InputStream inputStream, final DocumentMetadataDTO metadataDTO) throws IOException {
        return new TikaDocumentReader(new ByteArrayResource(inputStream.readAllBytes()))
                .read().stream()
                        .flatMap(document -> documentTextSplitter.splitDocumentIntoChunks(document.getText()).stream())
                        .map(chunk -> documentContentMapper.toDTO(chunk, metadataDTO.getDocumentId()))
                        .toList();
    }
}
