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
        var preprocessedContent = ContentUtil.preprocessContent(documentContent);
        return ContentUtil.splitContent(preprocessedContent, CHUNK_MAX_LENGTH).parallelStream()
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
