package ai.yda.framework.rag.retriever.website.service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ai.yda.framework.rag.core.retriever.util.ContentUtil;
import ai.yda.framework.rag.retriever.website.constants.Constants;
import ai.yda.framework.rag.retriever.website.exception.WebsiteReadException;

@Slf4j
public class WebsiteService {

    /**
     * Retrieves a map of document locations and their respective content chunks from the specified URL up to a certain depth.
     *
     * This method starts crawling from the provided URL, extracts links, connects to them safely, and processes the documents found.
     *
     * @param url the starting URL for crawling
     * @return a map where the key is the document location (URL) and the value is a list of processed content chunks
     */
    public Map<String, List<String>> getPageDocuments(final String url) {
        return documentFilterData(
                extractLinks(url).stream().map(this::safeConnect).collect(Collectors.toList()));
    }

    /**
     * Filters and processes the content of the given documents, splitting them into chunks.
     *
     * @param documents the list of documents to process
     * @return a map where the key is the document location (URL) and the value is a list of processed content chunks
     */
    private Map<String, List<String>> documentFilterData(final List<Document> documents) {
        var result = new HashMap<String, List<String>>();
        for (var document : documents) {
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
     * Extracts all links from the given sitemap URL.
     *
     * This method recursively processes sitemap index files to gather all individual URLs.
     *
     * @param url the URL of the sitemap
     * @return a set of URLs extracted from the sitemap
     */
    private Set<String> extractLinks(final String url) {
        var links = new HashSet<String>();
        try {
            var document = Jsoup.connect(url).get();
            var sitemapIndexElements = document.select("loc");

            for (var element : sitemapIndexElements) {
                if (element.text().contains("sitemap")) {
                    links.addAll(extractLinks(element.text()));
                } else {
                    return sitemapIndexElements.stream().map(Element::text).collect(Collectors.toSet());
                }
            }
            return links;

        } catch (IOException e) {
            throw new WebsiteReadException(e);
        }
    }

    /**
     * Safely connects to the given URL and retrieves its HTML Document.
     *
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
