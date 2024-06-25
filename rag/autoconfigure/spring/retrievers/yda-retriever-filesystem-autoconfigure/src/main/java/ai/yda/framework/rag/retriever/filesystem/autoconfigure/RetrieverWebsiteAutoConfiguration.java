package ai.yda.framework.rag.retriever.filesystem.autoconfigure;

import java.util.HashMap;

import ai.yda.framework.rag.retriever.filesystem.factory.WebsiteRetrieverFactory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.retriever.factory.RetrieverFactory;

import static ai.yda.framework.rag.retriever.filesystem.config.FilesystemRetrieverConfig.LOCAL_DIRECTORY_PATH;

@AutoConfiguration
@EnableConfigurationProperties({RetrieverWebsiteProperties.class})
public class RetrieverWebsiteAutoConfiguration {
    @Bean
    public Retriever<BaseAssistantRequest, BaseRagContext> filesystemRetriever(
            RetrieverFactory<BaseAssistantRequest, BaseRagContext> retrieverFactory,
            RetrieverWebsiteProperties properties) {

        return retrieverFactory.createRetriever(new HashMap<>() {
            {
                put(LOCAL_DIRECTORY_PATH, properties.getLocalDirectoryPath());
            }
        });
    }

    @Bean
    public RetrieverFactory<BaseAssistantRequest, BaseRagContext> retrieverFactory(VectorStore vectorStore) {
        return new WebsiteRetrieverFactory(vectorStore);
    }
}
