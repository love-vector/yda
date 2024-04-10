package org.vector.assistant.util.mapper;

import java.util.UUID;

import org.mapstruct.*;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.vector.assistant.dto.information.node.InformationNodeDto;
import org.vector.assistant.persistance.entity.InformationNodeEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InformationNodeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "informationNodeDto.name")
    @Mapping(target = "userId", source = "userId")
    @Mapping(
            target = "collectionName",
            expression = "java(informationNodeDto.name() + \"_\" + userId.toString().replace(\"-\", \"_\"))")
    InformationNodeEntity toEntity(final InformationNodeDto informationNodeDto, final UUID userId);

    @Mapping(target = "name", source = "entity.name")
    @Mapping(target = "description", source = "entity.description")
    InformationNodeDto toDto(final InformationNodeEntity entity);

    @Mapping(target = "name", source = "informationNodeDto.name")
    @Mapping(target = "description", source = "informationNodeDto.description")
    InformationNodeEntity updateInformationNode(
            @MappingTarget final InformationNodeEntity informationNode, final InformationNodeDto informationNodeDto);
}
