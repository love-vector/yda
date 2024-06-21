package ai.yda.framework.rag.core.retriever.factory;

import java.util.Map;

import ai.yda.common.shared.factory.FactoryConfig;
import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.retriever.Retriever;

public interface RetrieverFactory<REQUEST extends AssistantRequest, CONTEXT extends RagContext<?>> {
    Retriever<REQUEST, CONTEXT> createRetriever(Map<? extends FactoryConfig, String> config);
}
