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
import java.util.Optional;

import jakarta.transaction.Transactional;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentMetadataDTO;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentSummaryDTO;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentContentMapper;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentMetadataMapper;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentMetadataPort;
import ai.yda.framework.rag.retriever.google_drive.repository.DocumentMetadataRepository;

@Transactional
public class DocumentMetadataAdapter implements DocumentMetadataPort {

    private final DocumentMetadataRepository repository;
    private final DocumentMetadataMapper documentMetadataMapper;
    private final DocumentContentMapper documentContentMapper;

    public DocumentMetadataAdapter(
            final DocumentMetadataRepository repository,
            final DocumentMetadataMapper documentMetadataMapper,
            final DocumentContentMapper documentContentMapper) {
        this.repository = repository;
        this.documentMetadataMapper = documentMetadataMapper;
        this.documentContentMapper = documentContentMapper;
    }

    @Override
    public Optional<DocumentMetadataDTO> findById(String documentId) {
        return repository.findById(documentId).map(documentMetadataMapper::toDTO);
    }

    @Override
    public DocumentMetadataDTO save(DocumentMetadataDTO documentMetadataDTO) {
        var entity = documentMetadataMapper.toEntity(documentMetadataDTO);
        if (documentMetadataDTO.getParentId() != null) {
            entity.setParent(
                    repository.findById(documentMetadataDTO.getParentId()).orElse(null));
        }
        entity.setDocumentContents(documentMetadataDTO.getDocumentContents().stream()
                .map(documentContentDTO -> documentContentMapper.toEntity(documentContentDTO, entity))
                .toList());
        return documentMetadataMapper.toDTO(repository.save(entity));
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public List<DocumentSummaryDTO> getAllFileSummaries() {
        return repository.findAll().stream()
                .filter(documentMetadataEntity -> !documentMetadataEntity.isFolder())
                .map(documentMetadataMapper::toDto)
                .toList();
    }
}
