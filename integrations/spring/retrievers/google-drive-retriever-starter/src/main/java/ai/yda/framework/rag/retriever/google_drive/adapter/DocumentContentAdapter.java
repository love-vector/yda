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
package ai.yda.framework.rag.retriever.google_drive.adapter;

import java.util.List;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentContentDTO;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentContentIdDTO;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentIdsDTO;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentContentMapper;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentContentPort;
import ai.yda.framework.rag.retriever.google_drive.repository.DocumentContentRepository;

public class DocumentContentAdapter implements DocumentContentPort {

    private final DocumentContentRepository documentContentRepository;

    private final DocumentContentMapper documentContentMapper;

    public DocumentContentAdapter(
            final DocumentContentRepository documentContentRepository,
            final DocumentContentMapper documentContentMapper) {
        this.documentContentRepository = documentContentRepository;
        this.documentContentMapper = documentContentMapper;
    }

    @Override
    public List<DocumentContentDTO> getDocumentsContents(final DocumentIdsDTO documentIds) {
        return documentIds.documentIds().stream()
                .flatMap(documentId -> documentContentRepository.findByDocumentMetadata_DocumentId(documentId).stream())
                .map(documentContentMapper::toDTO)
                .toList();
    }

    @Override
    public List<DocumentContentDTO> getDocumentContentsByIds(final List<DocumentContentIdDTO> documentContentIdDTOs) {
        var documentContentIds = documentContentIdDTOs.stream()
                .map(DocumentContentIdDTO::contentId)
                .toList();
        return documentContentRepository.findAllById(documentContentIds).stream()
                .map(documentContentMapper::toDTO)
                .toList();
    }
}
