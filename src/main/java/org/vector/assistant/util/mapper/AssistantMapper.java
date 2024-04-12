package org.vector.assistant.util.mapper;

import java.util.UUID;

import com.theokanning.openai.assistants.Assistant;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import org.vector.assistant.dto.assistant.AssistantDTO;
import org.vector.assistant.persistance.entity.AssistantEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AssistantMapper {

    @Mapping(target = "id", source = "assistant.id")
    @Mapping(target = "name", source = "assistant.name")
    @Mapping(target = "instructions", source = "assistant.instructions")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "createdAt", ignore = true)
    AssistantEntity toEntity(final Assistant assistant, final UUID userId);

    @Mapping(target = "id", source = "assistant.id")
    @Mapping(target = "name", source = "assistant.name")
    @Mapping(target = "instructions", source = "assistant.instructions")
    @Mapping(target = "createdAt", source = "assistant.createdAt")
    AssistantDTO toDto(final AssistantEntity assistant);
}
