package ai.yda.llm.assistant;

import java.time.Instant;
import java.time.ZoneOffset;

import com.theokanning.openai.assistants.Assistant;
import com.theokanning.openai.assistants.AssistantRequest;
import com.theokanning.openai.assistants.ModifyAssistantRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {Instant.class, ZoneOffset.class})
public interface AssistantMapper {

    @Mapping(
            target = "createdAt",
            expression =
                    "java(OffsetDateTime.ofInstant(Instant.ofEpochSecond(assistant.getCreatedAt()), ZoneOffset.UTC))")
    AssistantDto toDto(final Assistant assistant);

    @Mapping(target = "model", constant = "gpt-4-1106-preview")
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "tools", ignore = true)
    @Mapping(target = "fileIds", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    AssistantRequest toCreateRequest(final AssistantDto assistantDto);

    @Mapping(target = "model", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "tools", ignore = true)
    @Mapping(target = "fileIds", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    ModifyAssistantRequest toModifyRequest(final AssistantDto assistantDto);
}
