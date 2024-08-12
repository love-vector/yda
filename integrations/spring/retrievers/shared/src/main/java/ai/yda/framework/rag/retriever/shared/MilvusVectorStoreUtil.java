package ai.yda.framework.rag.retriever.shared;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;

import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusServiceClientProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.vectorstore.MilvusVectorStore;

public final class MilvusVectorStoreUtil {

    private MilvusVectorStoreUtil() {}

    public static MilvusVectorStore createMilvusVectorStore(
            final RetrieverProperties retrieverProperties,
            final MilvusVectorStoreProperties milvusProperties,
            final MilvusServiceClientProperties milvusClientProperties,
            final OpenAiConnectionProperties openAiConnectionProperties,
            final OpenAiChatProperties openAiChatProperties) {

        var milvusClient = createMilvusClient(milvusClientProperties);
        var embeddingModel = createEmbeddingModel(openAiConnectionProperties, openAiChatProperties);

        var collectionName = retrieverProperties.getCollectionName();
        var databaseName = milvusProperties.getDatabaseName();

        var config = MilvusVectorStore.MilvusVectorStoreConfig.builder()
                .withCollectionName(collectionName)
                .withDatabaseName(databaseName)
                .withIndexType(IndexType.valueOf(milvusProperties.getIndexType().name()))
                .withMetricType(
                        MetricType.valueOf(milvusProperties.getMetricType().name()))
                .withEmbeddingDimension(milvusProperties.getEmbeddingDimension())
                .build();

        return new OptimizedMilvusVectorStore(
                milvusClient,
                embeddingModel,
                config,
                milvusProperties.isInitializeSchema(),
                collectionName,
                databaseName,
                retrieverProperties.getClearCollectionOnStartup());
    }

    private static EmbeddingModel createEmbeddingModel(
            final OpenAiConnectionProperties connectionProperties, final OpenAiChatProperties chatProperties) {
        var openAiApi = new OpenAiApi(connectionProperties.getApiKey());
        return new OpenAiEmbeddingModel(
                openAiApi,
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .withModel(chatProperties.getOptions().getModel())
                        .withUser("user")
                        .build(),
                RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    private static MilvusServiceClient createMilvusClient(final MilvusServiceClientProperties properties) {
        return new MilvusServiceClient(ConnectParam.newBuilder()
                .withAuthorization(properties.getUsername(), properties.getPassword())
                .withPort(properties.getPort())
                .withHost(properties.getHost())
                .build());
    }
}
