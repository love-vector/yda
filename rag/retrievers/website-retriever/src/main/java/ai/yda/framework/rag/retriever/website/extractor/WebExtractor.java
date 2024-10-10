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
package ai.yda.framework.rag.retriever.website.extractor;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import ai.yda.framework.rag.core.retriever.DataExtractor;
import ai.yda.framework.rag.retriever.website.extractor.model.ExtractionResult;
import ai.yda.framework.rag.retriever.website.extractor.pipeline.ResultingPipeline;
import ai.yda.framework.rag.retriever.website.extractor.processor.GeneralProcessor;
import ai.yda.framework.rag.retriever.website.extractor.util.ExtractionConstant;
import ai.yda.framework.rag.retriever.website.extractor.webmagic.ChromeSeleniumDownloader;

/**
 * A web data extractor that crawls web pages and extracts content based on provided configurations.
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
@Slf4j
public class WebExtractor implements DataExtractor<Set<ExtractionResult>> {

    /**
     * Maximum number of threads to be used by the web crawler for parallel processing.
     */
    private final Integer crawlerMaxThreads;

    /**
     * The processor responsible for processing web pages during the crawling process.
     * <p> Currently, using General Processor which allows processing both simple pages and sitemaps. </p>
     */
    private final PageProcessor processor;

    /**
     * The downloader responsible for downloading web pages during the crawling process.
     * <p> Currently, using Selenium Downloader for dynamic content download. </p>
     */
    private Downloader downloader;

    /**
     * Default constructor for {@link WebExtractor} with default crawler configuration.
     * <p>
     * This constructor initializes the extractor with default values for thread count, retry times, sleep times,
     * and crawling depth, as specified in {@link ExtractionConstant}.
     * </p>
     */
    public WebExtractor() {
        this(
                ExtractionConstant.CRAWLER_DEFAULT_MAX_THREADS,
                ExtractionConstant.CRAWLER_DEFAULT_RETRY_TIMES,
                ExtractionConstant.CRAWLER_DEFAULT_SLEEP_TIME,
                ExtractionConstant.CRAWLER_DEFAULT_MAX_DEPTH);
    }

    /**
     * Constructs a new {@link WebExtractor} instance with the specified configuration for the crawler.
     * <p>
     * This constructor is used when browser support is not required. It configures the crawler with the specified
     * thread count, retry times, sleep times, and crawling depth, and uses a default HTTP downloader.
     * </p>
     *
     * @param crawlerMaxThreads the maximum number of threads for the crawler.
     * @param crawlerRetryTimes the number of retries allowed for the crawler in case of failure.
     * @param crawlerSleepTime  the amount of time (in milliseconds) the crawler sleeps between requests.
     * @param crawlerMaxDepth   the maximum depth the crawler will follow links from the start page.
     */
    public WebExtractor(
            final Integer crawlerMaxThreads,
            final Integer crawlerRetryTimes,
            final Integer crawlerSleepTime,
            final Integer crawlerMaxDepth) {
        this.crawlerMaxThreads = crawlerMaxThreads;
        this.processor = new GeneralProcessor(crawlerRetryTimes, crawlerSleepTime, crawlerMaxDepth);
    }

    /**
     * Constructs a new {@link WebExtractor} instance with the specified configuration for the crawler
     * and browser settings.
     * <p>
     * This constructor is used when browser support is required for extracting dynamic content rendered by
     * JavaScript. It configures both the crawler and browser, allowing for multi-threaded extraction with
     * browser-based interactions.
     * </p>
     *
     * @param crawlerMaxThreads the maximum number of threads for the crawler.
     * @param crawlerRetryTimes the number of retries allowed for the crawler in case of failure.
     * @param crawlerSleepTime  the amount of time (in milliseconds) the crawler sleeps between requests.
     * @param crawlerMaxDepth   the maximum depth the crawler will follow links from the start page.
     * @param browserSleepTime  the amount of time (in milliseconds) the browser sleeps between actions.
     * @param browserPoolSize   the size of the browser instance pool for the web extraction process.
     */
    public WebExtractor(
            final Integer crawlerMaxThreads,
            final Integer crawlerRetryTimes,
            final Integer crawlerSleepTime,
            final Integer crawlerMaxDepth,
            final Integer browserSleepTime,
            final Integer browserPoolSize) {
        this.crawlerMaxThreads = crawlerMaxThreads;
        this.processor = new GeneralProcessor(crawlerRetryTimes, crawlerSleepTime, crawlerMaxDepth);
        this.downloader = new ChromeSeleniumDownloader(browserSleepTime, browserPoolSize);
    }

    /**
     * Extracts data from the specified source.
     *
     * @param source the URL of the web page or sitemap to crawl and extract content from.
     * @return a set of extracted {@link ExtractionResult} objects containing the processed results from the web source.
     */
    @Override
    public Set<ExtractionResult> extract(final String source) {
        var resultingPipeline = new ResultingPipeline();
        var spider = Spider.create(processor)
                .addUrl(source)
                .addPipeline(resultingPipeline)
                .thread(crawlerMaxThreads);
        if (log.isDebugEnabled()) {
            spider.addPipeline(new ConsolePipeline());
        }
        if (downloader != null) {
            spider.setDownloader(downloader);
        }
        spider.run();
        return resultingPipeline.getResults();
    }
}
