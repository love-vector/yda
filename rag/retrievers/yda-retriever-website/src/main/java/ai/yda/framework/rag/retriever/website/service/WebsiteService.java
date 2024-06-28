package ai.yda.framework.rag.retriever.website.service;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ai.yda.framework.rag.core.retriever.util.ContentUtil;
import ai.yda.framework.rag.retriever.website.constants.Constants;
import ai.yda.framework.rag.retriever.website.exception.WebsiteReadException;

public class WebsiteService {
    private static final Logger logger = Logger.getLogger(WebsiteService.class.getName());

    /**
     * Retrieves a list of Documents from the specified URL up to a given depth.
     *
     * @param url the URL to start crawling from
     * @return a list of Documents found within the specified depth
     */
    public List<Document> getPageDocuments(final String url) {
        return extractLinks(url).stream().map(this::safeConnect).collect(Collectors.toList());
    }

    /**
     * Filters and processes the content of the given documents.
     *
     * @param documents the list of documents to process
     * @return a map containing the document location and its processed content chunks
     */
    public Map<String, List<String>> documentFilterData(final List<Document> documents) {
        Map<String, List<String>> result = new HashMap<>();
        for (Document document : documents) {
            if (document != null) {
                var documentContent = document.text();
                var preprocessedContent = ContentUtil.preprocessContent(documentContent);
                var chunks = ContentUtil.splitContent(preprocessedContent, Constants.CHUNK_MAX_LENGTH);
                result.put(document.location(), chunks);
            }
        }
        return result;
    }

    /**
     * Extracts links from a sitemap.
     *
     * @param url the URL of the sitemap
     * @return a links of extracted links
     */
    private Set<String> extractLinks(final String url) {
        Set<String> links = new HashSet<>();
        try {
            var document = Jsoup.connect(url).get();
            var sitemapIndexElements = document.select("loc");

            for (Element element : sitemapIndexElements) {
                if (element.text().contains("sitemap")) {
                    links.addAll(extractLinks(element.text()));
                } else {
                    return sitemapIndexElements.stream().map(Element::text).collect(Collectors.toSet());
                }
            }
            return links;

        } catch (IOException e) {
            logger.info("Failed to retrieve sitemap from " + url);
        }
         return links;
    }

    /**
     * Safely connects to a URL and retrieves its Document.
     *
     * @param url the URL to connect to
     * @return the Document from the URL, or null if an error occurs
     */
    private Document safeConnect(final String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (HttpStatusException e) {
            logger.info("HTTP error fetching URL. Status=" + e.getStatusCode() + ", URL=" + url);
            throw new WebsiteReadException(e);
        } catch (IOException e) {
            logger.info("Error connecting to: " + url);
            throw new WebsiteReadException(e);
        }
    }
}
