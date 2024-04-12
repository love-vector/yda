package org.vector.assistant.util.mapper;

import java.util.UUID;

import org.mapstruct.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.vector.assistant.dto.InformationNodeDto;
import org.vector.assistant.persistance.entity.InformationNodeEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InformationNodeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "informationNodeDto.name")
    @Mapping(
            target = "collectionName",
            expression =
                    "java(informationNodeDto.name().trim().toLowerCase() + \"_\" + userId.toString().replace(\"-\", \"_\"))")
    @Mapping(target = "description", source = "informationNodeDto.description")
    @Mapping(target = "userId", source = "userId")
    InformationNodeEntity toEntity(final InformationNodeDto informationNodeDto, final UUID userId);

    default InformationNodeEntity updateEntity(
            final InformationNodeEntity informationNode, final InformationNodeDto informationNodeDto) {
        return informationNode.toBuilder()
                .name(informationNodeDto.name())
                .description(informationNodeDto.description())
                .build();
    }

    @Mapping(target = "name", source = "informationNode.name")
    @Mapping(target = "description", source = "informationNode.description")
    InformationNodeDto toDto(final InformationNodeEntity informationNode);
}
