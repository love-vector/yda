package org.vector.assistant.util.mapper;

import org.mapstruct.*;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import org.vector.assistant.dto.information.node.CreateInformationNodeRequest;
import org.vector.assistant.dto.information.node.InformationNodeDto;
import org.vector.assistant.persistance.entity.InformationNodeEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InformationNodeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "userId", source = "request.userId")
    InformationNodeEntity toEntity(final CreateInformationNodeRequest request);

    @Mapping(target = "name", source = "entity.name")
    @Mapping(target = "description", source = "entity.description")
    @Mapping(target = "userId", source = "entity.userId")
    InformationNodeDto toDto(final InformationNodeEntity entity);
}
