package ai.yda.framework.rag.retriever.website.factory;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.vectorstore.VectorStore;

import ai.yda.common.shared.factory.FactoryConfig;
import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.factory.RetrieverFactory;
import ai.yda.framework.rag.retriever.website.WebsiteRetriever;

import static ai.yda.framework.rag.retriever.website.config.WebsiteRetrieverConfig.IS_ENABLED;
import static ai.yda.framework.rag.retriever.website.config.WebsiteRetrieverConfig.WEBSITE_URL;

@RequiredArgsConstructor
public class WebsiteRetrieverFactory implements RetrieverFactory<BaseAssistantRequest, BaseRagContext> {

    private final VectorStore vectorStore;

    @Override
    public WebsiteRetriever createRetriever(Map<? extends FactoryConfig, String> config) {
        return new WebsiteRetriever(vectorStore, config.get(WEBSITE_URL), Boolean.parseBoolean(config.get(IS_ENABLED)));
    }
}
