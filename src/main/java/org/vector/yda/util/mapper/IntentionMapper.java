package org.vector.yda.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import org.vector.yda.model.dto.IntentionDto;
import org.vector.yda.model.response.DetermineIntentionResponse;
import org.vector.yda.persistance.entity.IntentionEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IntentionMapper {

    IntentionDto toDto(final IntentionEntity intention);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vectorId", ignore = true)
    IntentionEntity createEntity(final IntentionDto intention);

    DetermineIntentionResponse toDetermineResponse(final IntentionEntity intention, Float distance);
}
