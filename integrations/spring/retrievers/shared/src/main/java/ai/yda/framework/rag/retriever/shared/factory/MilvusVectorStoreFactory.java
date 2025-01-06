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
package ai.yda.framework.rag.retriever.shared.factory;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;

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

import ai.yda.framework.rag.retriever.shared.RetrieverMilvusVectorStore;
import ai.yda.framework.rag.retriever.shared.RetrieverProperties;
import ai.yda.framework.rag.retriever.shared.exception.MilvusVectorStoreException;

/**
 * Factory class for creating instances of {@link RetrieverMilvusVectorStore}.
 * Encapsulates the logic for creating and configuring {@link RetrieverMilvusVectorStore}.
 *
 * @author Iryna Kopchak
 * @author dmmrch
 * @see RetrieverMilvusVectorStore
 * @since 0.2.0
 */
public class MilvusVectorStoreFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private MilvusVectorStoreFactory() {}

    /**
     * Creates a configured instance of {@link RetrieverMilvusVectorStore}.
     *
     * @param retrieverProperties         the retriever-specific properties.
     * @param milvusVectorStoreProperties the Milvus vector store properties.
     * @param milvusClientProperties      the Milvus client connection properties.
     * @param openAiConnectionProperties  the OpenAI connection properties.
     * @param openAiEmbeddingProperties   the OpenAI embedding model properties.
     * @return a configured instance of {@link RetrieverMilvusVectorStore}.
     * @throws MilvusVectorStoreException if there are errors during creation.
     */
    public static RetrieverMilvusVectorStore createInstance(
            final RetrieverProperties retrieverProperties,
            final MilvusVectorStoreProperties milvusVectorStoreProperties,
            final MilvusServiceClientProperties milvusClientProperties,
            final OpenAiConnectionProperties openAiConnectionProperties,
            final OpenAiEmbeddingProperties openAiEmbeddingProperties) {
        try {

            var vectorStore = new RetrieverMilvusVectorStore(
                    createMilvusClient(milvusClientProperties),
                    createEmbeddingModel(openAiConnectionProperties, openAiEmbeddingProperties),
                    milvusVectorStoreProperties.isInitializeSchema(),
                    retrieverProperties.getCollectionName(),
                    milvusVectorStoreProperties.getDatabaseName(),
                    retrieverProperties.getDropCollectionOnStartup());

            // Call lifecycle method explicitly since Spring is not managing this bean
            vectorStore.afterPropertiesSet();

            return vectorStore;
        } catch (Exception e) {
            throw new MilvusVectorStoreException("Failed to create RetrieverMilvusVectorStore", e);
        }
    }

    private static MilvusServiceClient createMilvusClient(MilvusServiceClientProperties milvusClientProperties) {
        return new MilvusServiceClient(ConnectParam.newBuilder()
                .withAuthorization(milvusClientProperties.getUsername(), milvusClientProperties.getPassword())
                .withPort(milvusClientProperties.getPort())
                .withHost(milvusClientProperties.getHost())
                .build());
    }

    private static EmbeddingModel createEmbeddingModel(
            OpenAiConnectionProperties openAiConnectionProperties,
            OpenAiEmbeddingProperties openAiEmbeddingProperties) {
        var openAiApi = new OpenAiApi(openAiConnectionProperties.getApiKey());
        return new OpenAiEmbeddingModel(
                openAiApi,
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .model(openAiEmbeddingProperties.getOptions().getModel())
                        .user("user")
                        .build(),
                RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }
}
