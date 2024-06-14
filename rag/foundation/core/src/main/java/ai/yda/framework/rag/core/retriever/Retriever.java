package ai.yda.framework.rag.core.retriever;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.framework.rag.core.model.RagContext;

public interface Retriever<REQUEST extends AssistantRequest, CONTEXT extends RagContext> {

    CONTEXT retrieve(REQUEST request);
}
