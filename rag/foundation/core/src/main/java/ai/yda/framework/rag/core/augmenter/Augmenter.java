package ai.yda.framework.rag.core.augmenter;

import java.util.List;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.framework.rag.core.model.RagContext;

public interface Augmenter<REQUEST extends AssistantRequest, CONTEXT extends RagContext<?>> {

    REQUEST augment(REQUEST request, List<CONTEXT> contexts);
}
