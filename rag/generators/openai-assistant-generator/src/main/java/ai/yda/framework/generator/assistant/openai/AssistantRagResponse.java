package ai.yda.framework.generator.assistant.openai;

import lombok.Builder;
import lombok.Getter;

import ai.yda.framework.rag.core.model.RagResponse;

@Getter
@Builder(toBuilder = true)
public class AssistantRagResponse implements RagResponse {

    private String content;

    private String threadId;
}
