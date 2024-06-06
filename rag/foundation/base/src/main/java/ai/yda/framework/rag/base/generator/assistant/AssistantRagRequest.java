package ai.yda.framework.rag.base.generator.assistant;

import ai.yda.framework.rag.core.model.RagRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class AssistantRagRequest implements RagRequest {

    private String content;

    private String threadId;
}
