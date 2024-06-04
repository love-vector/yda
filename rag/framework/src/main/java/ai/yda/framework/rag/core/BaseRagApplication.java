package ai.yda.framework.rag.core;

import ai.yda.framework.rag.model.RagContext;
import ai.yda.framework.rag.model.RagRawContext;
import ai.yda.framework.rag.model.RagRequest;
import ai.yda.framework.rag.model.RagResponse;

public abstract class BaseRagApplication
        implements RagApplication<RagRequest, RagRawContext, RagContext, RagResponse> {}
