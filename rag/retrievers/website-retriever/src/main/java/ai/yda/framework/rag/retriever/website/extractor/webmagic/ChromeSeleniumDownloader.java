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
package ai.yda.framework.rag.retriever.website.extractor.webmagic;

import java.io.Closeable;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.PlainText;

import ai.yda.framework.rag.retriever.website.extractor.util.ExtractionConstant;
import ai.yda.framework.rag.retriever.website.extractor.util.WebUtil;

/**
 * A Selenium-based downloader that uses Chrome WebDriver to download web pages during the crawling process.
 * This downloader manages a pool of Chrome WebDriver instances to support parallel downloads.
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
@Slf4j
public class ChromeSeleniumDownloader extends AbstractDownloader implements Closeable {

    /**
     * The downloader used to handle sitemap pages. It uses an HTTP client to fetch the content of sitemap pages.
     */
    private final Downloader httpClientDownloader = new HttpClientDownloader();

    /**
     * Pool of Chrome WebDriver instances.
     */
    private volatile ChromeWebDriverPool webDriverPool;

    /**
     * Sleep time between browser actions, in milliseconds.
     */
    private final Integer sleepTime;

    /**
     * Number of WebDriver instances in the pool.
     */
    private Integer poolSize;

    /**
     * Default constructor that initializes the downloader with default sleep time and pool size.
     */
    public ChromeSeleniumDownloader() {
        this(ExtractionConstant.BROWSER_DEFAULT_SLEEP_TIME, ExtractionConstant.BROWSER_DEFAULT_POOL_SIZE);
    }

    /**
     * Constructs a downloader with the specified sleep time and pool size.
     *
     * @param sleepTime the sleep time between browser actions, in milliseconds.
     * @param poolSize  the number of WebDriver instances in the pool.
     */
    public ChromeSeleniumDownloader(final Integer sleepTime, final Integer poolSize) {
        WebDriverManager.chromedriver().setup();
        this.sleepTime = sleepTime;
        this.poolSize = poolSize;
        httpClientDownloader.setThread(poolSize);
    }

    /**
     * Downloads a simple page or a sitemap.
     * <p>
     * If the request URL points to a sitemap, it is downloaded using {@link HttpClientDownloader}. Otherwise,
     * the Chrome WebDriver is used to download simple pages.
     * </p>
     *
     * @param request the request representing the page or sitemap to download.
     * @param task    the crawling task being processed.
     * @return the downloaded {@link Page} object.
     */
    @Override
    public Page download(final Request request, final Task task) {
        if (WebUtil.isSitemapUrl(request.getUrl())) {
            log.debug("downloading SITEMAP page {}", request.getUrl());
            return httpClientDownloader.download(request, task);
        }
        if (webDriverPool == null) {
            synchronized (this) {
                webDriverPool = new ChromeWebDriverPool(poolSize);
            }
        }
        WebDriver webDriver = null;
        var page = Page.fail(request);
        try {
            webDriver = webDriverPool.get();
            log.debug("downloading SIMPLE page {}", request.getUrl());
            webDriver.get(request.getUrl());
            try {
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                log.error(e.getLocalizedMessage());
            }
            var manage = webDriver.manage();
            var site = task.getSite();
            if (site.getCookies() != null) {
                for (var cookieEntry : site.getCookies().entrySet()) {
                    var cookie = new Cookie(cookieEntry.getKey(), cookieEntry.getValue());
                    manage.addCookie(cookie);
                }
            }
            var webElement = webDriver.findElement(By.xpath("/html"));
            var content = webElement.getAttribute("outerHTML");
            page.setDownloadSuccess(true);
            page.setRawText(content);
            page.setUrl(new PlainText(request.getUrl()));
            page.setRequest(request);
            page.getHtml();
            onSuccess(page, task);
        } catch (Exception e) {
            log.error("download page {} error", request.getUrl(), e);
            onError(page, task, e);
        } finally {
            if (webDriver != null) {
                webDriverPool.returnToPool(webDriver);
            }
        }
        return page;
    }

    /**
     * Sets the pool size for WebDriver instances.
     *
     * @param thread the number of threads (WebDriver instances).
     */
    @Override
    public void setThread(final int thread) {
        this.poolSize = thread;
    }

    /**
     * Closes all WebDriver instances in the pool.
     */
    @Override
    public void close() {
        webDriverPool.closeAll();
    }
}
