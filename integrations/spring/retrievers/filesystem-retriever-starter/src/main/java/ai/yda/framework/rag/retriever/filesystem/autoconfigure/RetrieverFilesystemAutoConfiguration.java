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

import ai.yda.framework.rag.retriever.filesystem.DataFlowCoordinator;
import ai.yda.framework.rag.retriever.filesystem.indexing.FilesystemIndexing;
import ai.yda.framework.rag.retriever.filesystem.retriever.FilesystemRetriever;
import ai.yda.framework.rag.retriever.shared.MilvusVectorStoreUtil;
import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiEmbeddingProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusServiceClientProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

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
@EnableConfigurationProperties({RetrieverFilesystemProperties.class, DataFlowCoordinatorProperties.class})
public class RetrieverFilesystemAutoConfiguration {

    /**
     * Default constructor for {@link RetrieverFilesystemAutoConfiguration}.
     */
    public RetrieverFilesystemAutoConfiguration() {
    }

    @Bean
    public MilvusVectorStore milvusVectorStore(
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
        return milvusVectorStore;
    }

    @Bean
    public FilesystemRetriever filesystemRetriever(
            final RetrieverFilesystemProperties filesystemProperties, final MilvusVectorStore milvusVectorStore) {
        return new FilesystemRetriever(milvusVectorStore, filesystemProperties.getTopK());
    }

    @Bean
    public FilesystemIndexing filesystemIndexing(final MilvusVectorStore milvusVectorStore) {
        return new FilesystemIndexing(milvusVectorStore);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataFlowCoordinator dataFlowCoordinator(
            final FilesystemIndexing filesystemIndexing,
            final DataFlowCoordinatorProperties flowCoordinatorProperties) {
        return new DataFlowCoordinator(
                flowCoordinatorProperties.getDatasource(),
                filesystemIndexing,
                flowCoordinatorProperties.getIsProcessingEnabled(),
                flowCoordinatorProperties.getPipelineAlgorithm(),
                flowCoordinatorProperties.getChunkingAlgorithm());
    }
}