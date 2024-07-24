package ai.yda.framework.rag.retriever.filesystem.factory;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.vectorstore.VectorStore;

import ai.yda.common.shared.factory.FactoryConfig;
import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.factory.RetrieverFactory;
import ai.yda.framework.rag.retriever.filesystem.FilesystemRetriever;

import ai.yda.framework.rag.retriever.filesystem.config.FilesystemRetrieverConfig;

@RequiredArgsConstructor
public class FilesystemRetrieverFactory implements RetrieverFactory<BaseAssistantRequest, BaseRagContext> {

    private final VectorStore vectorStore;

    @Override
    public FilesystemRetriever createRetriever(final Map<? extends FactoryConfig, String> config) {
        return new FilesystemRetriever(config.get(FilesystemRetrieverConfig.LOCAL_DIRECTORY_PATH), vectorStore);
    }
}
