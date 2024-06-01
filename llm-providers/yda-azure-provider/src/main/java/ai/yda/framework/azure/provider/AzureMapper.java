package ai.yda.framework.azure.provider;

import java.util.stream.Collectors;

import com.azure.ai.openai.assistants.models.AssistantThread;
import com.azure.ai.openai.assistants.models.MessageTextContent;
import com.azure.ai.openai.assistants.models.ThreadMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ai.yda.framework.llm.Assistant;
import ai.yda.framework.llm.Message;
import ai.yda.framework.llm.Thread;

@Mapper(imports = {MessageTextContent.class, Collectors.class})
public interface AzureMapper {

    AzureMapper INSTANCE = Mappers.getMapper(AzureMapper.class);

    Assistant toAssistant(com.azure.ai.openai.assistants.models.Assistant assistant);

    Thread toThread(AssistantThread thread);

    @Mapping(
            target = "content",
            expression =
                    "java(message.getContent().stream().map(messageContent -> ((MessageTextContent) messageContent).getText().getValue()).collect(Collectors.joining(\". \")))")
    Message toMessage(ThreadMessage message);
}
