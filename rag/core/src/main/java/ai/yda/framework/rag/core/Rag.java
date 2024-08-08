package ai.yda.framework.rag.core;

import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

/**
 * The Rag coordinates the retrieval, augmentation, and generation
 * processes to produce a final response based on the user's query.
 */
public interface Rag<REQUEST extends RagRequest, RESPONSE extends RagResponse> {

    RESPONSE doRag(REQUEST request);
}
