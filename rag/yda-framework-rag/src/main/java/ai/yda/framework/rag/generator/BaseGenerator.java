package ai.yda.framework.rag.generator;

import ai.yda.framework.rag.dto.RagContext;
import ai.yda.framework.rag.dto.RagRequest;
import ai.yda.framework.rag.dto.RagResponse;

public abstract class BaseGenerator implements Generator<RagRequest, RagContext, RagResponse> {}
