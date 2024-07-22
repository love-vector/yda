package ai.yda.framework.rag.retriever.website.autoconfigure;

import java.util.Map;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.retriever.website.WebsiteRetriever;
import ai.yda.framework.rag.retriever.website.config.WebsiteRetrieverConfig;
import ai.yda.framework.rag.retriever.website.factory.WebsiteRetrieverFactory;

@AutoConfiguration
@EnableConfigurationProperties({RetrieverWebsiteProperties.class})
public class RetrieverWebsiteAutoConfiguration {
    @Bean
    public WebsiteRetriever websiteRetriever(
            final WebsiteRetrieverFactory retrieverFactory, final RetrieverWebsiteProperties properties) {
        return retrieverFactory.createRetriever(Map.of(
                WebsiteRetrieverConfig.WEBSITE_URL, properties.getUrl(),
                WebsiteRetrieverConfig.IS_CRAWLING_ENABLED, String.valueOf(properties.isCrawlingEnabled())));
    }

    @Bean
    public WebsiteRetrieverFactory websiteRetrieverFactory(final RetrieverWebsiteProperties properties) {
        var milvusClient = milvusClient(properties);
        var embeddingModel = embeddingModel(properties);
        var vectorStore = vectorStore(milvusClient, embeddingModel, properties);
        return new WebsiteRetrieverFactory(vectorStore);
    }

    private VectorStore vectorStore(
            final MilvusServiceClient milvusClient,
            final EmbeddingModel embeddingModel,
            final RetrieverWebsiteProperties properties) {
        var config = MilvusVectorStore.MilvusVectorStoreConfig.builder()
                .withCollectionName(properties.getCollectionName())
                .withDatabaseName(properties.getDatabaseName())
                .withIndexType(IndexType.IVF_FLAT)
                .withMetricType(MetricType.COSINE)
                .withEmbeddingDimension(properties.getEmbeddingDimension())
                .build();
        return new MilvusVectorStore(milvusClient, embeddingModel, config, Boolean.TRUE);
    }

    private EmbeddingModel embeddingModel(final RetrieverWebsiteProperties properties) {
        var openAiApi = new OpenAiApi(properties.getOpenAiKey());
        return new OpenAiEmbeddingModel(
                openAiApi,
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .withModel(properties.getOpenAiModel())
                        .withUser("user")
                        .build(),
                RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    private MilvusServiceClient milvusClient(final RetrieverWebsiteProperties properties) {
        return new MilvusServiceClient(ConnectParam.newBuilder()
                .withAuthorization(properties.getUsername(), properties.getPassword())
                .withUri(properties.getHost())
                .build());
    }
}
