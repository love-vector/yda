package ai.yda.knowledge.internal;

import org.mapstruct.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface KnowledgeMapper {

    KnowledgeDto toDto(final KnowledgeEntity knowledge);

    @Mapping(target = "id", ignore = true)
    KnowledgeEntity createEntity(final KnowledgeDto knowledgeDto);

    default KnowledgeEntity updateEntity(final KnowledgeEntity knowledge, final KnowledgeDto knowledgeDto) {
        return knowledge.toBuilder()
                .name(knowledgeDto.name())
                .description(knowledgeDto.description())
                .build();
    }
}
