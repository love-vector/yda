package ai.yda.framework.rag.core.augmenter;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;

public interface Augmenter<REQUEST extends RagRequest, CONTEXT extends RagContext> {

    CONTEXT augment(REQUEST request, CONTEXT context);
}
