package ai.yda.framework.rag.core.generator;

import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

public interface Generator<REQUEST extends RagRequest, RESPONSE extends RagResponse> {

    RESPONSE generate(REQUEST request);
}
