package ai.yda.framework.rag.base.retriever;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;

public interface Retriever<REQUEST extends RagRequest, CONTEXT extends RagContext> {

    CONTEXT retrieve(REQUEST request);
}
