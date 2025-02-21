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

import java.util.List;

import com.google.api.services.drive.model.File;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentAiDescriptionDTO;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentMetadataDTO;
import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;

@Mapper
public interface DocumentMetadataMapper {

    @Mapping(target = "documentId", source = "id")
    @Mapping(target = "parentId", source = "parents", qualifiedByName = "extractFirstParent")
    @Mapping(
            target = "createdAt",
            expression = "java(file.getCreatedTime() != null ? "
                    + "java.time.OffsetDateTime.ofInstant(java.time.Instant.ofEpochMilli(file.getCreatedTime().getValue()), "
                    + "java.time.ZoneOffset.ofTotalSeconds(file.getCreatedTime().getTimeZoneShift() * 60)) : "
                    + "java.time.OffsetDateTime.now())")
    @Mapping(
            target = "modifiedAt",
            expression = "java(file.getModifiedTime() != null ? "
                    + "java.time.OffsetDateTime.ofInstant(java.time.Instant.ofEpochMilli(file.getModifiedTime().getValue()), "
                    + "java.time.ZoneOffset.ofTotalSeconds(file.getModifiedTime().getTimeZoneShift() * 60)) : "
                    + "java.time.OffsetDateTime.now())")
    DocumentMetadataDTO toDTO(File file);

    /**
     * Converts a DocumentMetadataEntity to a DocumentMetadataDTO.
     *
     * @param entity the DocumentMetadataEntity to convert.
     * @return the converted DocumentMetadataDTO.
     */
    @Mapping(target = "parentId", source = "parent.documentId")
    DocumentMetadataDTO toDTO(DocumentMetadataEntity entity);

    /**
     * Converts a DocumentMetadataDTO to a DocumentMetadataEntity.
     *
     * @param dto the DocumentMetadataDTO to convert.
     * @return the converted DocumentMetadataEntity.
     */
    DocumentMetadataEntity toEntity(DocumentMetadataDTO dto);

    @Named("extractFirstParent")
    default String extractFirstParent(List<String> parents) {
        return (parents != null && !parents.isEmpty()) ? parents.get(0) : null;
    }

    @Mapping(target = "fileName", source = "name")
    DocumentAiDescriptionDTO toDto(DocumentMetadataEntity documentMetadataEntity);
}
