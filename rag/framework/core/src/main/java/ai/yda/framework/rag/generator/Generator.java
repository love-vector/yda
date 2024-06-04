package ai.yda.framework.rag.generator;

import ai.yda.framework.rag.model.RagContext;
import ai.yda.framework.rag.model.RagRequest;
import ai.yda.framework.rag.model.RagResponse;

public interface Generator<REQUEST extends RagRequest, CONTEXT extends RagContext, RESPONSE extends RagResponse> {

    RESPONSE generate(REQUEST request, CONTEXT context);
}
