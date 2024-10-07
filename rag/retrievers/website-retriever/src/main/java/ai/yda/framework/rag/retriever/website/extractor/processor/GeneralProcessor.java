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
package ai.yda.framework.rag.retriever.website.extractor.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import ai.yda.framework.rag.retriever.website.extractor.util.ExtractionConstant;
import ai.yda.framework.rag.retriever.website.extractor.util.WebUtil;

/**
 * A web page processor that handles the crawling and processing of both sitemaps and simple web pages.
 * <p>
 * This processor extracts links from sitemaps and follows them to a specified depth, while also extracting text
 * content from simple pages.
 * </p>
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
public class GeneralProcessor implements PageProcessor {

    /**
     * The configuration for the crawling site, including retry and sleep settings.
     */
    private final Site site;

    /**
     * The maximum depth for crawling links from the starting simple page.
     */
    private final Integer maxDepth;

    /**
     * Constructs a {@link GeneralProcessor} with default retry, sleep, and max depth values.
     */
    public GeneralProcessor() {
        this(
                ExtractionConstant.CRAWLER_DEFAULT_RETRY_TIMES,
                ExtractionConstant.CRAWLER_DEFAULT_SLEEP_TIME,
                ExtractionConstant.CRAWLER_DEFAULT_MAX_DEPTH);
    }

    /**
     * Constructs a {@link GeneralProcessor} with custom retry, sleep, and max depth values.
     *
     * @param retryTimes the number of times to retry in case of failure.
     * @param sleepTime  the sleep time in milliseconds between requests.
     * @param maxDepth   the maximum depth to follow links from the starting simple page.
     */
    public GeneralProcessor(final Integer retryTimes, final Integer sleepTime, final Integer maxDepth) {
        this.site = Site.me().setRetryTimes(retryTimes).setSleepTime(sleepTime);
        this.maxDepth = maxDepth;
    }

    /**
     * Processes the web page by either extracting links from a sitemap or processing a simple page.
     *
     * @param page the page to process.
     */
    @Override
    public void process(final Page page) {
        if (WebUtil.isSitemapUrl(page.getUrl().get())) {
            processSitemap(page);
        } else {
            processSimplePage(page);
        }
    }

    /**
     * Extracts and adds links from a sitemap page as targets for further crawling.
     *
     * @param page the sitemap page.
     */
    protected void processSitemap(final Page page) {
        var locationUrls = page.getHtml().xpath("//loc/text()").all();
        locationUrls.stream()
                .map(link -> {
                    var depth = WebUtil.isSitemapUrl(link)
                            ? ExtractionConstant.SITEMAP_DEPTH
                            : ExtractionConstant.SIMPLE_PAGE_MIN_DEPTH;
                    return new Request(link).putExtra(ExtractionConstant.DEPTH_KEY, depth);
                })
                .forEach(page::addTargetRequest);
    }

    /**
     * Processes a simple web page, extracts and adds links as targets for further crawling, and extracts the text
     * content from the page.
     *
     * @param page the simple page to process.
     */
    protected void processSimplePage(final Page page) {
        // process links
        var currentDepth = page.getRequest().getExtra(ExtractionConstant.DEPTH_KEY) == null
                ? ExtractionConstant.SIMPLE_PAGE_MIN_DEPTH
                : (Integer) page.getRequest().getExtra(ExtractionConstant.DEPTH_KEY);
        if (currentDepth < maxDepth) {
            page.getHtml().links().all().stream()
                    .filter(link -> link.startsWith(page.getRequest().getUrl()))
                    .map(link -> {
                        var request = new Request(link);
                        request.putExtra(ExtractionConstant.DEPTH_KEY, currentDepth + 1);
                        return request;
                    })
                    .forEach(page::addTargetRequest);
        }
        page.putField(ExtractionConstant.DEPTH_KEY, currentDepth);
        // process text
        var pageText = page.getHtml().xpath("//*//text()").all().stream().reduce("", (acc, text) -> acc + " " + text);
        page.putField(ExtractionConstant.PAGE_TEXT_KEY, pageText);
    }

    /**
     * Returns the site configuration for the processor.
     *
     * @return the {@link Site} configuration.
     */
    @Override
    public Site getSite() {
        return site;
    }
}
