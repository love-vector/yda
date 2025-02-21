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
package ai.yda.framework.rag.retriever.google_drive.mapper;

import java.util.HashMap;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.springframework.ai.document.Document;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentContentDTO;
import ai.yda.framework.rag.retriever.google_drive.entity.DocumentContentEntity;
import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;

@Mapper
public interface DocumentContentMapper {

    @Mapping(target = "contentId", ignore = true)
    @Mapping(target = "chunkName", source = "chunkName")
    @Mapping(target = "chunkContent", source = "chunkContent")
    @Mapping(target = "documentMetadataId", source = "documentMetadataId")
    DocumentContentDTO toDTO(String chunkName, String chunkContent, String documentMetadataId);

    @Mapping(target = "contentId", ignore = true)
    @Mapping(target = "chunkName", ignore = true)
    @Mapping(target = "chunkContent", source = "chunkContent")
    @Mapping(target = "documentMetadataId", source = "documentMetadataId")
    DocumentContentDTO toDTO(String chunkContent, String documentMetadataId);

    @Mapping(target = "documentName", source = "documentMetadata.name")
    DocumentContentDTO toDTO(DocumentContentEntity documentContentEntity);

    List<DocumentContentDTO> toDTOs(List<DocumentContentEntity> documentContentEntities);

    default Document toDocument(DocumentContentEntity documentContentEntity) {
        var metadata = new HashMap<String, Object>();
        metadata.put("documentName", documentContentEntity.getDocumentMetadata().getName());
        if (documentContentEntity.getChunkName() != null) {
            metadata.put("chunkName", documentContentEntity.getChunkName());
        }
        var documentMetadataEntity = documentContentEntity.getDocumentMetadata();
        if (documentMetadataEntity != null) {
            metadata.put("fileName", documentMetadataEntity.getName());
            metadata.put("webViewLink", documentMetadataEntity.getWebViewLink());
        }
        return new Document(documentContentEntity.getChunkContent(), metadata);
    }

    List<Document> toDocuments(List<DocumentContentEntity> documentContentEntities);

    @Mapping(target = "documentMetadata", source = "documentMetadata")
    DocumentContentEntity toEntity(DocumentContentDTO documentMetadataDTO, DocumentMetadataEntity documentMetadata);
}
