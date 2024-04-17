package org.vector.assistant.util.mapper;

import java.util.UUID;

import org.mapstruct.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.vector.assistant.model.dto.InformationNodeDto;
import org.vector.assistant.persistance.entity.InformationNodeEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InformationNodeMapper {

    @Mapping(
            target = "collectionName",
            expression =
                    "java(informationNodeDto.name().trim().toLowerCase() + \"_\" + userId.toString().replace(\"-\", \"_\"))")
    InformationNodeEntity toEntity(final InformationNodeDto informationNodeDto, final UUID userId);

    default InformationNodeEntity updateEntity(
            final InformationNodeEntity informationNode, final InformationNodeDto informationNodeDto) {
        return informationNode.toBuilder()
                .name(informationNodeDto.name())
                .description(informationNodeDto.description())
                .build();
    }

    InformationNodeDto toDto(final InformationNodeEntity informationNode);
}
