package ai.yda.framework.rag.core.generator;

import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

/**
 * The Generator takes the user's query and the retrieved context to produce a final
 * response, often by leveraging a language model or other generative mechanism.
 */
public interface Generator<REQUEST extends RagRequest, RESPONSE extends RagResponse> {

    RESPONSE generate(REQUEST request, String context);
}
