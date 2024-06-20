package ai.yda.framework.rag.retriever.filesystem.factory;

import java.util.Map;

import ai.yda.framework.rag.retriever.filesystem.WebSiteRetriever;
import lombok.RequiredArgsConstructor;

import org.springframework.ai.vectorstore.VectorStore;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.retriever.factory.RetrieverConfig;
import ai.yda.framework.rag.core.retriever.factory.RetrieverFactory;

import static ai.yda.framework.rag.retriever.filesystem.config.WebsiteRetrieverConfig.LOCAL_DIRECTORY_PATH;

@RequiredArgsConstructor
public class FilesystemRetrieverFactory implements RetrieverFactory<BaseAssistantRequest, BaseRagContext> {

    private final VectorStore vectorStore;

    @Override
    public Retriever<BaseAssistantRequest, BaseRagContext> createRetriever(
            Map<? extends RetrieverConfig, String> config) {
        return new WebSiteRetriever(config.get(LOCAL_DIRECTORY_PATH), vectorStore);
    }
}
