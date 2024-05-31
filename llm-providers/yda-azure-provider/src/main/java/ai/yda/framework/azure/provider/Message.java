package ai.yda.framework.azure.provider;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import com.azure.ai.openai.assistants.models.MessageTextContent;
import com.azure.ai.openai.assistants.models.ThreadMessage;
import lombok.Builder;

import ai.yda.framework.llm.MessagePrototype;

@Builder
public class Message implements MessagePrototype {

    private ThreadMessage message;

    @Override
    public String getId() {
        return message.getId();
    }

    @Override
    public String getThreadId() {
        return message.getThreadId();
    }

    @Override
    public String getContent() {
        return message.getContent().stream()
                .map(content -> ((MessageTextContent) content).getText().getValue())
                .collect(Collectors.joining(". "));
    }

    @Override
    public OffsetDateTime getCreatedAt() {
        return message.getCreatedAt();
    }
}
