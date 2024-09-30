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
package ai.yda.framework.rag.retriever.website.extractor.util;

/**
 * Contains constants used in the extraction process, including default configurations for crawler and browser.
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
public final class ExtractionConstant {

    /**
     * Key for storing the depth of the page during the crawling process.
     */
    public static final String DEPTH_KEY = "pageDepth";

    /**
     * Key for storing the extracted text content of a page.
     */
    public static final String PAGE_TEXT_KEY = "pageText";

    /**
     * Depth value for sitemap URLs.
     */
    public static final Integer SITEMAP_DEPTH = 0;

    /**
     * Minimum depth value for simple pages.
     */
    public static final Integer SIMPLE_PAGE_MIN_DEPTH = 1;

    /**
     * Default number of retries for the crawler when fetching a page fails.
     */
    public static final Integer CRAWLER_DEFAULT_RETRY_TIMES = 3;

    /**
     * Default sleep time (in milliseconds) between crawler requests.
     */
    public static final Integer CRAWLER_DEFAULT_SLEEP_TIME = 300;

    /**
     * Default maximum depth the crawler will follow links from the starting simple page.
     */
    public static final Integer CRAWLER_DEFAULT_MAX_DEPTH = 1;

    /**
     * Default sleep time (in milliseconds) between browser actions.
     */
    public static final Integer BROWSER_DEFAULT_SLEEP_TIME = 500;

    /**
     * Default number of browser instances in the pool for web extraction.
     */
    public static final Integer BROWSER_DEFAULT_POOL_SIZE = 3;

    /**
     * Private constructor to prevent instantiation.
     */
    private ExtractionConstant() {}
}
