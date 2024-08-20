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
package ai.yda.framework.rag.retriever.website.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import org.springframework.ai.document.Document;

import ai.yda.framework.rag.core.util.ContentUtil;
import ai.yda.framework.rag.retriever.website.exception.WebsiteReadException;

/**
 * Provides methods to process website sitemap, specifically for creating chunked documents from website's pages. This
 * class handles the retrieval of sitemaps and individual web pages, processes their content, and splits it into
 * manageable chunks. Each chunk is then converted into a {@link Document} object. This service is used for indexing and
 * analyzing content from websites based on their sitemaps.
 *
 * @author Iryna Kopchak
 * @author Bogdan Synenko
 * @see ContentUtil
 * @since 0.1.0
 */
@Slf4j
public class WebsiteService {

    /**
     * The maximum length of a chunk in characters.
     */
    public static final int CHUNK_MAX_LENGTH = 1000;

    /**
     * Default constructor for {@link WebsiteService}.
     */
    public WebsiteService() {}

    /**
     * Processes a sitemap urls and creates chunked {@link Document} objects from them. This method connects to the
     * provided sitemap URL, retrieves and parses it to find individual website URLs. Each URL is then processed to
     * either recursively handle other sitemaps or directly split website pages into chunks.
     *
     * @param sitemapUrl a sitemap url to be processed.
     * @return a list of {@link Document} objects created from the chunks of website urls.
     */
    public List<Document> createChunkDocumentsFromSitemapUrl(final String sitemapUrl) {
        var document = safeConnect(sitemapUrl);
        var sitemapIndexElements = document.select("loc");
        return sitemapIndexElements.parallelStream()
                .map(element -> {
                    var currentUrl = element.text();
                    return currentUrl.contains("sitemap")
                            ? createChunkDocumentsFromSitemapUrl(currentUrl)
                            : splitWebsitePageIntoChunksDocuments(currentUrl);
                })
                .flatMap(List::stream)
                .toList();
    }

    /**
     * Preprocesses and split of each website page into chunks of a maximum length defined by {@link #CHUNK_MAX_LENGTH}.
     * The method connects to the specified website page URL, retrieves its content, preprocesses it, and then splits
     * it into smaller chunks based on the maximum length. Each chunk is converted into a {@link Document} with
     * metadata about the source URL.
     *
     * @param websitePageUrl the website page url to be processed.
     * @return a list of {@link Document} objects created from the chunks of the website page.
     */
    private List<Document> splitWebsitePageIntoChunksDocuments(final String websitePageUrl) {
        var documentContent = safeConnect(websitePageUrl).text();
        if (documentContent.trim().isEmpty()) {
            return Collections.emptyList();
        }
        log.debug("Processing website's page url: {}", websitePageUrl);
        var preprocessedContent = ContentUtil.preprocessContent(documentContent);
        return ContentUtil.splitContent(preprocessedContent, CHUNK_MAX_LENGTH).parallelStream()
                .map(chunkContent -> new Document(chunkContent, Map.of("url", websitePageUrl)))
                .toList();
    }

    /**
     * Safely connects to the given URL and retrieve its HTML Document.
     *
     * @param url the URL to connect to.
     * @return the {@link org.jsoup.nodes.Document} object retrieved from the URL.
     * @throws WebsiteReadException if an IOException occurs during the connection attempt.
     */
    private org.jsoup.nodes.Document safeConnect(final String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("HTTP error fetching URL: {}", e.getMessage());
            throw new WebsiteReadException(e);
        }
    }
}
