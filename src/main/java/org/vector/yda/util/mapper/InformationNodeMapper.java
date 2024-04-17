package org.vector.yda.util.mapper;

import java.util.UUID;

import org.mapstruct.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.vector.yda.model.dto.InformationNodeDto;
import org.vector.yda.persistance.entity.InformationNodeEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InformationNodeMapper {

    InformationNodeDto toDto(final InformationNodeEntity informationNode);

    @Mapping(target = "id", ignore = true)
    @Mapping(
            target = "collectionName",
            expression =
                    "java(informationNodeDto.name().trim().toLowerCase() + \"_\" + userId.toString().replace(\"-\", \"_\"))")
    InformationNodeEntity createEntity(final InformationNodeDto informationNodeDto, final UUID userId);

    default InformationNodeEntity updateEntity(
            final InformationNodeEntity informationNode, final InformationNodeDto informationNodeDto) {
        return informationNode.toBuilder()
                .name(informationNodeDto.name())
                .description(informationNodeDto.description())
                .build();
    }
}
