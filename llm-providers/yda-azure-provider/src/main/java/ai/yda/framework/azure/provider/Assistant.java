package ai.yda.framework.azure.provider;

import java.time.OffsetDateTime;

import lombok.Builder;

import ai.yda.framework.llm.AssistantPrototype;

@Builder
public class Assistant implements AssistantPrototype {

    private com.azure.ai.openai.assistants.models.Assistant assistant;

    @Override
    public String getId() {
        return assistant.getId();
    }

    @Override
    public String getName() {
        return assistant.getName();
    }

    @Override
    public String getInstructions() {
        return assistant.getInstructions();
    }

    @Override
    public OffsetDateTime getCreatedAt() {
        return assistant.getCreatedAt();
    }
}
