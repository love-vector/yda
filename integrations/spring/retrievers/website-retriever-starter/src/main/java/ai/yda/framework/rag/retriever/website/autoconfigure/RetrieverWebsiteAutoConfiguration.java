package ai.yda.framework.rag.retriever.website.autoconfigure;

import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusServiceClientProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.retriever.shared.MilvusVectorStoreUtil;
import ai.yda.framework.rag.retriever.website.WebsiteRetriever;

@AutoConfiguration
@EnableConfigurationProperties(RetrieverWebsiteProperties.class)
public class RetrieverWebsiteAutoConfiguration {
    @Bean
    public WebsiteRetriever websiteRetriever(
            final RetrieverWebsiteProperties websiteProperties,
            final MilvusVectorStoreProperties milvusProperties,
            final MilvusServiceClientProperties milvusClientProperties,
            final OpenAiConnectionProperties openAiConnectionProperties,
            final OpenAiChatProperties openAiChatProperties)
            throws Exception {

        var milvusVectorStore = MilvusVectorStoreUtil.createMilvusVectorStore(
                websiteProperties,
                milvusProperties,
                milvusClientProperties,
                openAiConnectionProperties,
                openAiChatProperties);
        milvusVectorStore.afterPropertiesSet();
        return new WebsiteRetriever(
                milvusVectorStore,
                websiteProperties.getSitemapUrl(),
                websiteProperties.getTopK(),
                websiteProperties.getIsProcessingEnabled());
    }
}
