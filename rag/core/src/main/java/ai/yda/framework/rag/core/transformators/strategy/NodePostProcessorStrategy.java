package ai.yda.framework.rag.core.transformators.strategy;

import ai.yda.framework.rag.core.model.RagContext;

import java.util.List;

public interface NodePostProcessorStrategy {
    List<RagContext> retrieveRagContext(List<RagContext> ragContext);
}
