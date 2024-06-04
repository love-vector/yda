package ai.yda.framework.rag.augmenter;

import ai.yda.framework.rag.model.RagContext;
import ai.yda.framework.rag.model.RagRequest;

public interface Augmenter<REQUEST extends RagRequest, CONTEXT extends RagContext> {

    CONTEXT augment(REQUEST request, CONTEXT context);
}
