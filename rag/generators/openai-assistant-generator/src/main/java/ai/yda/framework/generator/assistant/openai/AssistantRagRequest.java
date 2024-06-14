package ai.yda.framework.generator.assistant.openai;

import lombok.Builder;
import lombok.Getter;

import ai.yda.framework.rag.core.model.RagRequest;

@Getter
@Builder(toBuilder = true)
public class AssistantRagRequest implements RagRequest {

    private String content;

    private String threadId;
}
