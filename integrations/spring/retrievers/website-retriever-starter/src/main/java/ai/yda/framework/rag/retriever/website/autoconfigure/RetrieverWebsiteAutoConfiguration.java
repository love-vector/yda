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
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.retriever.shared.factory.MilvusVectorStoreFactory;
import ai.yda.framework.rag.retriever.website.WebsiteRetriever;
import ai.yda.framework.rag.retriever.website.extractor.WebExtractor;

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
@EnableConfigurationProperties({RetrieverWebsiteProperties.class, WebExtractorProperties.class})
public class RetrieverWebsiteAutoConfiguration {

    /**
     * Default constructor for {@link RetrieverWebsiteAutoConfiguration}.
     */
    public RetrieverWebsiteAutoConfiguration() {}

    @Bean
    public WebsiteRetriever websiteRetriever(
            final WebExtractor webExtractor,
            final RetrieverWebsiteProperties websiteProperties,
            final MilvusVectorStoreProperties milvusProperties,
            final MilvusServiceClientProperties milvusClientProperties,
            final OpenAiConnectionProperties openAiConnectionProperties,
            final OpenAiEmbeddingProperties openAiEmbeddingProperties) {

        return new WebsiteRetriever(
                webExtractor,
                MilvusVectorStoreFactory.createInstance(
                        websiteProperties,
                        milvusProperties,
                        milvusClientProperties,
                        openAiConnectionProperties,
                        openAiEmbeddingProperties),
                websiteProperties.getUrl(),
                websiteProperties.getTopK(),
                websiteProperties.getIsProcessingEnabled());
    }

    /**
     * Creates a {@link WebExtractor} bean with the specified properties.
     * <p>
     * The {@link WebExtractor} is configured using the provided {@link WebExtractorProperties},
     * which defines the crawler's behavior, such as the maximum number of threads, retry times,
     * sleep times, depth limits, and browser support.
     * </p>
     * <p>
     * If browser support is enabled, the {@link WebExtractor} will be configured to use browser-based crawling for
     * extracting dynamic content with a specified pool size and sleep times for the browser. Otherwise, it will be
     * configured for non-browser crawling.
     * </p>
     *
     * @param crawlerProperties the properties used to configure the {@link WebExtractor}.
     * @return a configured {@link WebExtractor} instance.
     */
    @Bean
    @ConditionalOnMissingBean
    public WebExtractor webExtractor(final WebExtractorProperties crawlerProperties) {
        if (crawlerProperties.getBrowserSupportEnabled()) {
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
