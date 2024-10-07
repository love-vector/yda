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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import ai.yda.framework.rag.retriever.website.extractor.util.ExtractionConstant;

/**
 * A pool manager for Chrome WebDriver instances. It manages the creation, reuse, and closure of WebDriver instances,
 * allowing efficient handling of multiple browser sessions during web crawling.
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
@Slf4j
public class ChromeWebDriverPool {

    private static final Integer STAT_RUNNING = 1;

    private static final Integer STAT_CLOSED = 2;

    /**
     * Maximum number of WebDriver instances allowed in the pool.
     */
    private final Integer capacity;

    /**
     * The current status of the pool (running or closed).
     */
    private final AtomicInteger stat = new AtomicInteger(STAT_RUNNING);

    /**
     * The WebDriver instance used in the pool.
     */
    private WebDriver mDriver = null;

    /**
     * List of created WebDriver instances.
     */
    private final List<WebDriver> webDriverList = Collections.synchronizedList(new ArrayList<>());

    /**
     * Queue of available WebDriver instances.
     */
    private final BlockingDeque<WebDriver> innerQueue = new LinkedBlockingDeque<>();

    /**
     * Default constructor that initializes the pool with the default capacity.
     */
    public ChromeWebDriverPool() {
        this(ExtractionConstant.BROWSER_DEFAULT_POOL_SIZE);
    }

    /**
     * Constructs a WebDriver pool with the specified capacity.
     *
     * @param capacity the maximum number of WebDriver instances allowed in the pool.
     */
    public ChromeWebDriverPool(final Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * Configures a new Chrome WebDriver.
     *
     * @throws IOException if an error occurs during configuration.
     */
    public void configure() throws IOException {
        var options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--incognito");
        options.addArguments("--no-sandbox");
        mDriver = new ChromeDriver(options);
    }

    /**
     * Retrieves a WebDriver instance from the pool, or creates a new one if the pool is not yet full.
     *
     * @return the WebDriver instance.
     * @throws InterruptedException if interrupted while waiting for an available WebDriver.
     */
    public WebDriver get() throws InterruptedException {
        checkRunning();
        var poll = innerQueue.poll();
        if (poll != null) {
            return poll;
        }
        if (webDriverList.size() < capacity) {
            synchronized (webDriverList) {
                if (webDriverList.size() < capacity) {
                    try {
                        configure();
                        innerQueue.add(mDriver);
                        webDriverList.add(mDriver);
                    } catch (IOException e) {
                        log.error(e.getLocalizedMessage());
                    }
                }
            }
        }
        return innerQueue.take();
    }

    /**
     * Returns a WebDriver instance back to the pool.
     *
     * @param webDriver the WebDriver instance to return.
     */
    public void returnToPool(final WebDriver webDriver) {
        checkRunning();
        innerQueue.add(webDriver);
    }

    /**
     * Ensures that the pool is still running.
     *
     * @throws IllegalStateException if the pool is already closed.
     */
    protected void checkRunning() {
        if (!stat.compareAndSet(STAT_RUNNING, STAT_RUNNING)) {
            throw new IllegalStateException("Already closed!");
        }
    }

    /**
     * Closes all WebDriver instances in the pool and marks the pool as closed.
     *
     * @throws IllegalStateException if the pool is already closed.
     */
    public void closeAll() {
        var b = stat.compareAndSet(STAT_RUNNING, STAT_CLOSED);
        if (!b) {
            throw new IllegalStateException("Already closed!");
        }
        for (var webDriver : webDriverList) {
            log.debug("Quit webDriver{}", webDriver);
            webDriver.quit();
        }
    }
}
