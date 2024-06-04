package ai.yda.framework.rag.retriever;

import ai.yda.framework.rag.dto.RagRawContext;
import ai.yda.framework.rag.dto.RagRequest;

public abstract class BaseRetriever implements Retriever<RagRequest, RagRawContext> {}
