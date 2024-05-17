package ai.yda.llm.thread;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ThreadMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "threadId", source = "openAiThreadId")
    ThreadEntity createEntity(final String openAiThreadId, final Long assistantId);
}
