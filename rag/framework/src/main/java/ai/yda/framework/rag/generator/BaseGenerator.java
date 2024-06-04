package ai.yda.framework.rag.generator;

import ai.yda.framework.rag.model.RagContext;
import ai.yda.framework.rag.model.RagRequest;
import ai.yda.framework.rag.model.RagResponse;

public abstract class BaseGenerator implements Generator<RagRequest, RagContext, RagResponse> {}
