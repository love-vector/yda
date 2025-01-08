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

import com.google.api.services.drive.model.File;
import org.mapstruct.*;

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentMetadataMapper {

    @Mapping(target = "documentId", source = "file.id")
    @Mapping(target = "uri", source = "file.webViewLink")
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
    DocumentMetadataEntity toEntity(File file);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    DocumentMetadataEntity updateEntity(DocumentMetadataEntity source, @MappingTarget DocumentMetadataEntity target);
}
