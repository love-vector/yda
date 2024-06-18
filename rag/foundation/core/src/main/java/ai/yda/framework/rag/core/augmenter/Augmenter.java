package ai.yda.framework.rag.core.augmenter;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.framework.rag.core.model.RagContext;

public interface Augmenter<REQUEST extends AssistantRequest, CONTEXT extends RagContext> {

    REQUEST augment(REQUEST request, CONTEXT context);
}
