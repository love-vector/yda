package ai.yda.framework.rag.core.retriever;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;

/**
 * Retriever is responsible for fetching relevant data or documents that can provide
 * additional information or context based on the user's query.
 */
public interface Retriever<REQUEST extends RagRequest, CONTEXT extends RagContext> {

    CONTEXT retrieve(REQUEST request);
}
