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
package ai.yda.framework.rag.retriever.website.autoconfigure;

import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiEmbeddingProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusServiceClientProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.retriever.shared.MilvusVectorStoreUtil;
import ai.yda.framework.rag.retriever.website.DataFlowCoordinator;
import ai.yda.framework.rag.retriever.website.extractor.WebExtractor;
import ai.yda.framework.rag.retriever.website.indexing.WebsiteIndexing;
import ai.yda.framework.rag.retriever.website.retriever.WebsiteRetriever;

/**
 * Autoconfiguration class for creating and configuring all necessary components for Web Retrieving.
 *
 * @author Iryna Kopchak
 * @author Bogdan Synenko
 * @author Nikita Litvinov
 * @see WebsiteRetriever
 * @see WebExtractor
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties({
    RetrieverWebsiteProperties.class,
    WebExtractorProperties.class,
    DataFlowCoordinatorProperties.class,
    WebsiteIndexingProperties.class
})
public class RetrieverWebsiteAutoConfiguration {

    public RetrieverWebsiteAutoConfiguration() {}

    @Bean
    public MilvusVectorStore milvusVectorStore(
            final RetrieverWebsiteProperties websiteProperties,
            final MilvusVectorStoreProperties milvusProperties,
            final MilvusServiceClientProperties milvusClientProperties,
            final OpenAiConnectionProperties openAiConnectionProperties,
            final OpenAiEmbeddingProperties openAiEmbeddingProperties)
            throws Exception {

        var milvusVectorStore = MilvusVectorStoreUtil.createMilvusVectorStore(
                websiteProperties,
                milvusProperties,
                milvusClientProperties,
                openAiConnectionProperties,
                openAiEmbeddingProperties);
        milvusVectorStore.afterPropertiesSet();
        return milvusVectorStore;
    }

    @Bean
    public WebsiteRetriever websiteRetriever(
            final RetrieverWebsiteProperties websiteProperties, final MilvusVectorStore milvusVectorStore) {
        return new WebsiteRetriever(milvusVectorStore, websiteProperties.getTopK());
    }

    @Bean
    public WebsiteIndexing websiteIndexing(final MilvusVectorStore milvusVectorStore) {
        return new WebsiteIndexing(milvusVectorStore);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataFlowCoordinator dataFlowCoordinator(
            final DataFlowCoordinatorProperties dataFlowCoordinatorProperties,
            final WebExtractor webExtractor,
            final WebsiteIndexing websiteIndexing) {
        return new DataFlowCoordinator(
                dataFlowCoordinatorProperties.getDatasource(),
                websiteIndexing,
                webExtractor,
                dataFlowCoordinatorProperties.getIsProcessingEnabled(),
                dataFlowCoordinatorProperties.getPipelineAlgorithm(),
                dataFlowCoordinatorProperties.getChunkingAlgorithm());
    }

    @Bean
    @ConditionalOnMissingBean
    public WebExtractor webExtractor(final WebExtractorProperties crawlerProperties) {
        if (Boolean.TRUE.equals(crawlerProperties.getBrowserSupportEnabled())) {
            return new WebExtractor(
                    crawlerProperties.getCrawlerMaxThreads(),
                    crawlerProperties.getCrawlerRetryTimes(),
                    crawlerProperties.getCrawlerSleepTime(),
                    crawlerProperties.getCrawlerMaxDepth(),
                    crawlerProperties.getBrowserSleepTime(),
                    crawlerProperties.getBrowserPoolSize());
        }
        return new WebExtractor(
                crawlerProperties.getCrawlerMaxThreads(),
                crawlerProperties.getCrawlerRetryTimes(),
                crawlerProperties.getCrawlerSleepTime(),
                crawlerProperties.getCrawlerMaxDepth());
    }
}
