package org.vector.yda.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import org.vector.yda.persistance.entity.ThreadEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ThreadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "threadId", source = "openAiThreadId")
    @Mapping(target = "assistantId", source = "assistantId")
    ThreadEntity createEntity(final String openAiThreadId, final Long assistantId);
}
