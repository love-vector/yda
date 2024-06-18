package ai.yda.framework.rag.retriever.filesystem.factory;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.vectorstore.VectorStore;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.retriever.factory.RetrieverConfig;
import ai.yda.framework.rag.core.retriever.factory.RetrieverFactory;
import ai.yda.framework.rag.retriever.filesystem.FilesystemRetriever;

import static ai.yda.framework.rag.retriever.filesystem.config.FilesystemRetrieverConfig.LOCAL_DIRECTORY_PATH;

@RequiredArgsConstructor
public class FilesystemRetrieverFactory implements RetrieverFactory<BaseAssistantRequest, BaseRagContext> {

    private final VectorStore vectorStore;

    @Override
    public Retriever<BaseAssistantRequest, BaseRagContext> createRetriever(
            Map<? extends RetrieverConfig, String> config) {
        return new FilesystemRetriever(config.get(LOCAL_DIRECTORY_PATH), vectorStore);
    }
}
