package ai.yda.framework.azure.provider;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

import com.theokanning.openai.messages.MessageContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ai.yda.framework.llm.Assistant;
import ai.yda.framework.llm.Message;
import ai.yda.framework.llm.Thread;

@Mapper(imports = {Instant.class, ZoneOffset.class, Collectors.class, OffsetDateTime.class, MessageContent.class})
public interface TheokanningMapper {

    TheokanningMapper INSTANCE = Mappers.getMapper(TheokanningMapper.class);

    @Mapping(
            target = "createdAt",
            expression =
                    "java(OffsetDateTime.ofInstant(Instant.ofEpochSecond(assistant.getCreatedAt()), ZoneOffset.UTC))")
    Assistant toAssistant(com.theokanning.openai.assistants.Assistant assistant);

    @Mapping(
            target = "createdAt",
            expression = "java(OffsetDateTime.ofInstant(Instant.ofEpochSecond(thread.getCreatedAt()), ZoneOffset.UTC))")
    Thread toThread(com.theokanning.openai.threads.Thread thread);

    @Mapping(
            target = "content",
            expression =
                    "java(message.getContent().stream().map(messageContent -> ((MessageContent) messageContent).getText().getValue()).collect(Collectors.joining(\". \")))")
    @Mapping(
            target = "createdAt",
            expression =
                    "java(OffsetDateTime.ofInstant(Instant.ofEpochSecond(message.getCreatedAt()), ZoneOffset.UTC))")
    Message toMessage(com.theokanning.openai.messages.Message message);
}
