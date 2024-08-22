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

@Slf4j
public class WebsiteService {

    public static final int CHUNK_MAX_LENGTH = 1000;

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

    private List<Document> splitWebsitePageIntoChunksDocuments(final String websitePageUrl) {
        var documentContent = safeConnect(websitePageUrl).text();
        if (documentContent.trim().isEmpty()) {
            return Collections.emptyList();
        }
        log.debug("Processing website's page url: {}", websitePageUrl);
        return ContentUtil.preprocessAndSplitContent(documentContent, CHUNK_MAX_LENGTH).parallelStream()
                .map(chunkContent -> new Document(chunkContent, Map.of("url", websitePageUrl)))
                .toList();
    }

    /**
     * Safely connects to the given URL and retrieves its HTML Document.
     * This method handles IOExceptions by logging the error and throwing a custom WebsiteReadException.
     *
     * @param url the URL to connect to
     * @return the Document object retrieved from the URL, or null if an error occurs
     * @throws WebsiteReadException if an IOException occurs during the connection attempt
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
