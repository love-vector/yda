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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

    DocumentContentDTO toDTO(DocumentContentEntity documentContentEntity);

    @Mapping(target = "documentMetadata", source = "documentMetadata")
    DocumentContentEntity toEntity(DocumentContentDTO documentMetadataDTO, DocumentMetadataEntity documentMetadata);
}
