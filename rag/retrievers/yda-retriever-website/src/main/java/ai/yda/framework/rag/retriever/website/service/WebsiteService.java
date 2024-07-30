package ai.yda.framework.rag.retriever.website.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import ai.yda.framework.rag.core.retriever.util.ContentUtil;
import ai.yda.framework.rag.retriever.website.exception.WebsiteReadException;

@Slf4j
public class WebsiteService {

    public static final int CHUNK_MAX_LENGTH = 1000;

    public Set<String> createContentChunks(final String url) {
        var documents = ConcurrentHashMap.<String>newKeySet();

        var document = safeConnect(url);
        var sitemapIndexElements = document.select("loc");

        sitemapIndexElements.parallelStream().forEach(element -> {
            var link = element.text();
            if (link.contains("sitemap")) {
                documents.addAll(createContentChunks(link));
            } else {
                var documentChunks = splitDocumentIntoChunks(safeConnect(link));
                documents.addAll(documentChunks);
            }
        });
        return documents;
    }

    private List<String> splitDocumentIntoChunks(final Document document) {
        if (document != null) {
            var documentContent = document.text();
            if (!documentContent.trim().isEmpty()) {
                var preprocessedContent = ContentUtil.preprocessContent(documentContent);
                return ContentUtil.splitContent(preprocessedContent, CHUNK_MAX_LENGTH);
            }
        }
        return Collections.emptyList();
    }

    /**
     * Safely connects to the given URL and retrieves its HTML Document.
     * This method handles IOExceptions by logging the error and throwing a custom WebsiteReadException.
     *
     * @param url the URL to connect to
     * @return the Document object retrieved from the URL, or null if an error occurs
     * @throws WebsiteReadException if an IOException occurs during the connection attempt
     */
    private Document safeConnect(final String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("HTTP error fetching URL: {}", e.getMessage());
            throw new WebsiteReadException(e);
        }
    }
}
