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
package ai.yda.framework.rag.retriever.filesystem.autoconfigure;

import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiEmbeddingProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusServiceClientProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.retriever.filesystem.FilesystemRetriever;
import ai.yda.framework.rag.retriever.shared.MilvusVectorStoreUtil;

/**
 * Autoconfiguration class for setting up the {@link FilesystemRetriever} bean with the necessary properties and
 * dependencies. The configuration is based on properties defined in the external configuration files
 * <p>
 * The configuration is based on properties defined in the external configuration files (e.g., application.properties
 * or application.yml) under {@link RetrieverFilesystemProperties#CONFIG_PREFIX},
 * {@link MilvusVectorStoreProperties#CONFIG_PREFIX} and {@link OpenAiConnectionProperties#CONFIG_PREFIX} namespaces.
 * </p>
 *
 * @author Iryna Kopchak
 * @author Dmitry Marchuk
 * @see FilesystemRetriever
 * @see RetrieverFilesystemProperties
 * @see MilvusVectorStoreProperties
 * @see MilvusServiceClientProperties
 * @see OpenAiConnectionProperties
 * @see OpenAiEmbeddingProperties
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(RetrieverFilesystemProperties.class)
public class RetrieverFilesystemAutoConfiguration {

    /**
     * Default constructor for {@link RetrieverFilesystemAutoConfiguration}.
     */
    public RetrieverFilesystemAutoConfiguration() {}

    /**
     * Creates and configures an instance of {@link FilesystemRetriever} using the provided properties and services.
     *
     * <p>This method performs the following steps:</p>
     * <ul>
     *     <li>Creates a {@code MilvusVectorStore} instance using the provided properties and services.</li>
     *     <li>Initializes the {@code MilvusVectorStore} instance by calling {@code afterPropertiesSet()}.</li>
     *     <li>Creates and returns a {@link FilesystemRetriever} instance with the initialized parameters</li>
     * </ul>
     *
     * @param filesystemProperties       properties for configuring the {@link FilesystemRetriever}, including
     *                                   collectionName, topK, isIndexingEnabled, clearCollectionOnStartup and
     *                                   fileStoragePath settings.
     * @param milvusProperties           properties for configuring the Milvus Vector Store.
     * @param milvusClientProperties     properties for configuring the Milvus Service Client.
     * @param openAiConnectionProperties properties for configuring the OpenAI connection.
     * @param openAiEmbeddingProperties  properties for configuring the OpenAI Embeddings.
     * @return a fully configured {@link FilesystemRetriever} instance.
     * @throws Exception if an error occurs during initialization.
     */
    @Bean
    public FilesystemRetriever filesystemRetriever(
            final RetrieverFilesystemProperties filesystemProperties,
            final MilvusVectorStoreProperties milvusProperties,
            final MilvusServiceClientProperties milvusClientProperties,
            final OpenAiConnectionProperties openAiConnectionProperties,
            final OpenAiEmbeddingProperties openAiEmbeddingProperties)
            throws Exception {

        var milvusVectorStore = MilvusVectorStoreUtil.createMilvusVectorStore(
                filesystemProperties,
                milvusProperties,
                milvusClientProperties,
                openAiConnectionProperties,
                openAiEmbeddingProperties);
        milvusVectorStore.afterPropertiesSet();
        return new FilesystemRetriever(
                milvusVectorStore,
                filesystemProperties.getFileStoragePath(),
                filesystemProperties.getTopK(),
                filesystemProperties.getIsIndexingEnabled(),
                filesystemProperties.getChunkingAlgorithm());
    }
}
