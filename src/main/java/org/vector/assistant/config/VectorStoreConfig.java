package org.vector.assistant.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import lombok.RequiredArgsConstructor;

import org.springframework.ai.document.id.IdGenerator;
import org.springframework.ai.document.id.JdkSha256HexIdGenerator;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.vector.assistant.persistance.dao.MilvusDao;

@Configuration
@RequiredArgsConstructor
public class VectorStoreConfig {

    private final MilvusDao milvusDao;

    private final MilvusServiceClient milvusServiceClient;

    private final EmbeddingClient embeddingClient;

    @Bean
    public IdGenerator idGenerator() {
        return new JdkSha256HexIdGenerator();
    }

    @Bean(name = Collection.INTENTIONS)
    public VectorStore intentionVectorStore() {
        return createVectorStoreIfNotExists(Collection.INTENTIONS);
    }

    private VectorStore createVectorStoreIfNotExists(final String collectionName) {
        milvusDao.createCollectionIfNotExist(collectionName);
        var config = MilvusVectorStore.MilvusVectorStoreConfig.builder()
                .withDatabaseName(MilvusVectorStore.DEFAULT_DATABASE_NAME)
                .withCollectionName(collectionName)
                .withIndexType(IndexType.IVF_FLAT)
                .withMetricType(MetricType.COSINE)
                .withEmbeddingDimension(MilvusVectorStore.OPENAI_EMBEDDING_DIMENSION_SIZE)
                .build();
        return new MilvusVectorStore(milvusServiceClient, embeddingClient, config);
    }

    public static class Collection {
        public static final String INTENTIONS = "intentions";
    }
}
