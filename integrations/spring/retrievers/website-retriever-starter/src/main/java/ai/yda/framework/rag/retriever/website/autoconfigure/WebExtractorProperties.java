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

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import ai.yda.framework.rag.retriever.website.extractor.util.ExtractionConstant;

/**
 * Provides configuration properties for WebExtractor (website Retriever module). These properties can be customized
 * through the application’s external configuration, such as a properties file, YAML file, or environment variables.
 * The properties include settings for controlling crawler threads, retry behavior, sleep time, and depth limitations.
 * <p>
 * The properties are prefixed with {@link #CONFIG_PREFIX} and can be customized by defining values under this prefix
 * in the external configuration.
 * <pre>
 * Example configuration in a YAML file:
 * ai:
 *    yda:
 *      framework:
 *         rag:
 *            retriever:
 *                website:
 *                    data:
 *                        extraction:
 *                            crawlerMaxThreads: your-max-threads
 *                            crawlerRetryTimes: your-retry-times
 *                            crawlerSleepTime: your-sleep-time
 *                            crawlerMaxDepth: your-max-depth
 *                            browserSleepTime: your-browser-sleep-time
 *                            browserPoolSize: your-browser-pool-size
 * </pre>
 * This class allows for the customization of key parameters for efficient and tailored web data extraction
 * processes.
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
@Getter
@Setter
@ConfigurationProperties(WebExtractorProperties.CONFIG_PREFIX)
public class WebExtractorProperties {

    /**
     * The configuration prefix used to reference properties related to the WebExtractor in application
     * configurations. This prefix is used for binding properties within the specific namespace.
     */
    public static final String CONFIG_PREFIX = RetrieverWebsiteProperties.CONFIG_PREFIX + ".data.extraction";

    /**
     * Maximum number of threads that the crawler can use for parallel webpages processing.
     */
    private Integer crawlerMaxThreads = ExtractionConstant.CRAWLER_DEFAULT_MAX_THREADS;

    /**
     * The number of times the crawler will retry fetching a page upon failure.
     */
    private Integer crawlerRetryTimes = ExtractionConstant.CRAWLER_DEFAULT_RETRY_TIMES;

    /**
     * The amount of time in milliseconds the crawler sleeps between requests.
     */
    private Integer crawlerSleepTime = ExtractionConstant.CRAWLER_DEFAULT_SLEEP_TIME;

    /**
     * The maximum depth the crawler will follow links from the start page.
     */
    private Integer crawlerMaxDepth = ExtractionConstant.CRAWLER_DEFAULT_MAX_DEPTH;

    /**
     * Indicates whether browser support is enabled for extracting dynamic content.
     */
    private Boolean browserSupportEnabled = ExtractionConstant.BROWSER_SUPPORT_ENABLED;

    /**
     * The amount of time in milliseconds the browser sleeps between actions during the extraction process.
     */
    private Integer browserSleepTime = ExtractionConstant.BROWSER_DEFAULT_SLEEP_TIME;

    /**
     * The size of the pool of browser instances available for web extraction.
     */
    private Integer browserPoolSize = ExtractionConstant.BROWSER_DEFAULT_POOL_SIZE;

    /**
     * Default constructor for {@link WebExtractorProperties}.
     */
    public WebExtractorProperties() {}
}
