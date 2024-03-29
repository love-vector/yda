package org.vector.assistant.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import org.vector.assistant.model.dto.IntentionDto;
import org.vector.assistant.model.response.DetermineIntentionResponse;
import org.vector.assistant.persistance.entity.IntentionEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IntentionMapper {

    IntentionDto toDto(final IntentionEntity intention);

    @Mapping(target = "vectorId", ignore = true)
    IntentionEntity toEntity(final IntentionDto intention);

    DetermineIntentionResponse toDetermineResponse(final IntentionEntity intention, Float distance);
}
