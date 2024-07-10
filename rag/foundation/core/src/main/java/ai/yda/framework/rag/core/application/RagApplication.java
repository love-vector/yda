package ai.yda.framework.rag.core.application;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.framework.rag.core.model.RagContext;

public interface RagApplication<REQUEST extends AssistantRequest, CONTEXT extends RagContext<?>, RESPONSE> {

    RESPONSE doRag(REQUEST request);
}
