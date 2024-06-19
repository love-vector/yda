package ai.yda.framework.rag.retriever.website.factory;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.vectorstore.VectorStore;

import ai.yda.common.shared.factory.FactoryConfig;
import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.retriever.factory.RetrieverFactory;
import ai.yda.framework.rag.retriever.website.WebsiteRetriever;

@RequiredArgsConstructor
public class WebsiteRetrieverFactory implements RetrieverFactory<BaseAssistantRequest, BaseRagContext> {

    private final VectorStore vectorStore;

    @Override
    public Retriever<BaseAssistantRequest, BaseRagContext> createRetriever(
            Map<? extends FactoryConfig, String> config) {
        return new WebsiteRetriever(vectorStore);
    }
}
