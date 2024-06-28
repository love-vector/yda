package ai.yda.framework.rag.retriever.website.autoconfigure;

import java.util.HashMap;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import ai.yda.framework.rag.retriever.website.WebsiteRetriever;
import ai.yda.framework.rag.retriever.website.factory.WebsiteRetrieverFactory;

import static ai.yda.framework.rag.retriever.website.config.WebsiteRetrieverConfig.WEBSITE_URL;
import static org.springframework.ai.retry.RetryUtils.DEFAULT_RETRY_TEMPLATE;

@AutoConfiguration
@EnableConfigurationProperties({RetrieverWebsiteProperties.class})
public class RetrieverWebsiteAutoConfiguration {
    @Bean
    public WebsiteRetriever websiteRetriever(
            WebsiteRetrieverFactory retrieverFactory, RetrieverWebsiteProperties properties) {
        return retrieverFactory.createRetriever(new HashMap<>() {
            {
                put(WEBSITE_URL, properties.getUrl());
            }
        });
    }

    @Bean
    public WebsiteRetrieverFactory websiteRetrieverFactory(VectorStore vectorStore) {
        return new WebsiteRetrieverFactory(vectorStore);
    }

    @Bean
    @Primary
    public VectorStore websiteVectorStore(
            MilvusServiceClient milvusClient, EmbeddingModel embeddingModel, RetrieverWebsiteProperties properties) {
        MilvusVectorStore.MilvusVectorStoreConfig config = MilvusVectorStore.MilvusVectorStoreConfig.builder()
                .withCollectionName(properties.getCollectionName())
                .withDatabaseName(properties.getDatabaseName())
                .withIndexType(IndexType.IVF_FLAT)
                .withMetricType(MetricType.COSINE)
                .withEmbeddingDimension(properties.getEmbeddingDimension())
                .build();
        return new MilvusVectorStore(milvusClient, embeddingModel, config, Boolean.TRUE);
    }

    @Bean
    @Primary
    public EmbeddingModel embeddingModel(RetrieverWebsiteProperties properties) {
        var openAiApi = new OpenAiApi(properties.getOpenAiKey());
        return new OpenAiEmbeddingModel(
                openAiApi,
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .withModel(properties.getOpenAiModel())
                        .withUser("user")
                        .build(),
                DEFAULT_RETRY_TEMPLATE);
    }

    @Bean
    @Primary
    public MilvusServiceClient milvusClient(RetrieverWebsiteProperties properties) {
        return new MilvusServiceClient(ConnectParam.newBuilder()
                .withAuthorization(properties.getUsername(), properties.getPassword())
                .withUri(properties.getHost())
                .build());
    }
}
