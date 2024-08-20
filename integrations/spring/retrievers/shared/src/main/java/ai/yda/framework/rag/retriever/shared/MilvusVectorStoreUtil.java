/*
 * YDA - Open-Source Java AI Assistant.
 * Copyright (C) 2024 Love Vector OÜ <https://vector-inc.dev/>

 * This file is part of YDA.

 * YDA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * YDA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with YDA.  If not, see <https://www.gnu.org/licenses/>.
*/
package ai.yda.framework.rag.retriever.shared;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;

import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiEmbeddingProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusServiceClientProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.vectorstore.MilvusVectorStore;

/**
 * Utility class for creating and configuring instances of {@link MilvusVectorStore}. This class provides static methods
 * to initialize a {@link MilvusVectorStore} with the necessary configuration and dependencies.
 *
 * @author Iryna Kopchak
 * @see MilvusVectorStore
 * @see RetrieverProperties
 * @see MilvusVectorStore
 * @see OptimizedMilvusVectorStore
 * @since 0.1.0
 */
public final class MilvusVectorStoreUtil {

    private MilvusVectorStoreUtil() {}

    /**
     * Constructs a new {@link MilvusVectorStore} instance with the specified properties and configurations.
     *
     * @param retrieverProperties        the properties related to the Retriever configuration.
     * @param milvusProperties           the properties specific to Milvus configuration.
     * @param milvusClientProperties     the properties for Milvus Service Client.
     * @param openAiConnectionProperties the properties for connecting to OpenAI API.
     * @param openAiEmbeddingProperties  the properties related to OpenAI Embedding Model.
     * @return a configured {@link MilvusVectorStore} instance.
     */
    public static MilvusVectorStore createMilvusVectorStore(
            final RetrieverProperties retrieverProperties,
            final MilvusVectorStoreProperties milvusProperties,
            final MilvusServiceClientProperties milvusClientProperties,
            final OpenAiConnectionProperties openAiConnectionProperties,
            final OpenAiEmbeddingProperties openAiEmbeddingProperties) {

        var milvusClient = createMilvusClient(milvusClientProperties);
        var embeddingModel = createEmbeddingModel(openAiConnectionProperties, openAiEmbeddingProperties);

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

    /**
     * Creates an {@link EmbeddingModel} for OpenAI using the specified connection and embedding properties.
     *
     * @param connectionProperties      the properties for connecting to OpenAI API.
     * @param openAiEmbeddingProperties the properties related to OpenAI embedding model.
     * @return a configured {@link EmbeddingModel} instance.
     */
    private static EmbeddingModel createEmbeddingModel(
            final OpenAiConnectionProperties connectionProperties,
            final OpenAiEmbeddingProperties openAiEmbeddingProperties) {
        var openAiApi = new OpenAiApi(connectionProperties.getApiKey());
        return new OpenAiEmbeddingModel(
                openAiApi,
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .withModel(openAiEmbeddingProperties.getOptions().getModel())
                        .withUser("user")
                        .build(),
                RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * Creates a {@link MilvusServiceClient} using the specified connection properties.
     *
     * @param properties the properties for Milvus service client connection.
     * @return a configured {@link MilvusServiceClient} instance.
     */
    private static MilvusServiceClient createMilvusClient(final MilvusServiceClientProperties properties) {
        return new MilvusServiceClient(ConnectParam.newBuilder()
                .withAuthorization(properties.getUsername(), properties.getPassword())
                .withPort(properties.getPort())
                .withHost(properties.getHost())
                .build());
    }
}
