package ai.yda.framework.rag.base.generator.assistant;

import ai.yda.framework.rag.core.model.RagResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class AssistantRagResponse implements RagResponse {

    private String content;

    private String threadId;
}
