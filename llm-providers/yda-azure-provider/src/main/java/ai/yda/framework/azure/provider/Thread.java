package ai.yda.framework.azure.provider;

import java.time.OffsetDateTime;

import com.azure.ai.openai.assistants.models.AssistantThread;
import lombok.Builder;

import ai.yda.framework.llm.ThreadPrototype;

@Builder
public class Thread implements ThreadPrototype {

    private AssistantThread thread;

    @Override
    public String getId() {
        return thread.getId();
    }

    @Override
    public OffsetDateTime getCreatedAt() {
        return thread.getCreatedAt();
    }
}
