package ai.yda.llm.assistant;

import com.theokanning.openai.assistants.ModifyAssistantRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AssistantMapper {

    AssistantDto toDto(final AssistantEntity assistant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "assistantId", source = "openAiAssistantId")
    AssistantEntity createEntity(final AssistantDto assistantDto, final String openAiAssistantId);

    default AssistantEntity updateEntity(final AssistantEntity assistant, final AssistantDto assistantDto) {
        return assistant.toBuilder()
                .name(assistantDto.name())
                .instructions(assistantDto.instructions())
                .build();
    }

    @Mapping(target = "model", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "tools", ignore = true)
    @Mapping(target = "fileIds", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    ModifyAssistantRequest toModifyAssistantRequest(final AssistantEntity assistant);
}
