package ai.yda.framework.rag.retriever;

import ai.yda.framework.rag.model.RagContext;
import ai.yda.framework.rag.model.RagRequest;

public interface Retriever<REQUEST extends RagRequest, CONTEXT extends RagContext> {

    CONTEXT retrieve(REQUEST request);
}
