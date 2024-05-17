package ai.yda.intent.internal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IntentMapper {

    IntentDto toDto(final IntentEntity intent);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vectorId", ignore = true)
    IntentEntity createEntity(final IntentDto intentDto);

    DetermineIntentResponse toDetermineResponse(final IntentEntity intent, Float distance);
}
