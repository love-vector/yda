package ai.yda.framework.rag.retriever.filesystem.service;

import ai.yda.framework.rag.retriever.filesystem.exception.WebsiteReadException;
import ai.yda.framework.rag.retriever.filesystem.util.ContentUtil;
import ai.yda.framework.rag.retriever.filesystem.util.WebsiteUtil;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WebsiteService {
    private static final Logger logger = Logger.getLogger(WebsiteService.class.getName());
    private final Set<String> links;
    private static final int MAX_DEPTH = 20;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public WebsiteService() {
        links = new HashSet<>();
    }

    public Set<String> getPageLinks(String URL, int depth) {
        synchronized (links) {
            if ((!links.contains(URL) && (depth < MAX_DEPTH))) {
                links.add(URL);
                logger.info("Fetching URL: " + URL + " at depth: " + depth);
            } else {
                return links;
            }
        }

        try {
            Document document = Jsoup.connect(URL).get();
            Elements linksOnPage = document.select("a[href]");

            int nextDepth = depth + 1;
            List<CompletableFuture<Void>> futures = linksOnPage.stream()
                    .map(page -> page.attr("abs:href"))
                    .filter(this::isValidURL)
                    .map(link -> CompletableFuture.runAsync(() -> {
                        try {
                            getPageLinks(link, nextDepth);
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error fetching link: " + link, e);
                        }
                    }, executor))
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error fetching URL: " + URL, e);
            throw new WebsiteReadException(e);
        }
        return links;
    }

    public void processDocument(Document document) {
        StringBuilder sb = new StringBuilder();
        document.select("h1, h2, h3, h4, h5, h6").forEach(heading -> sb.append("Heading: ").append(heading.text()).append("\n"));
        document.select("a[href]").forEach(link -> sb.append("Link: ").append(link.attr("abs:href")).append(" Text: ").append(link.text()).append("\n"));
        document.select("p").forEach(paragraph -> sb.append("Paragraph: ").append(paragraph.text()).append("\n"));
        logger.info("Processed document: \n" + sb.toString());
    }

    public List<Document> getPageDocuments(String url, int depth) {
        logger.info("Starting to fetch documents from URL: " + url + " with depth: " + depth);
        Set<String> linker = getPageLinks(url, depth);
        List<CompletableFuture<Document>> futures = linker.stream()
                .map(link -> CompletableFuture.supplyAsync(() -> safeConnect(link), executor))
                .collect(Collectors.toList());

        List<Document> documents = futures.stream()
                .map(CompletableFuture::join)
                .filter(doc -> doc != null)
                .collect(Collectors.toList());

        logger.info("Finished fetching documents from URL: " + url);
        return documents;
    }

    private Document safeConnect(String url) {
        try {
            logger.info("Connecting to URL: " + url);
            return Jsoup.connect(url).get();
        } catch (HttpStatusException e) {
            logger.log(Level.WARNING, "HTTP error fetching URL. Status=" + e.getStatusCode() + ", URL=" + url, e);
            return null;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error connecting to URL: " + url, e);
            return null;
        }
    }

    private boolean isValidURL(String url) {
        try {
            URI uri = new URI(url);
            if (!uri.isAbsolute() || uri.getScheme() == null || uri.getHost() == null) {
                return false;
            }
            String path = uri.getPath() != null ? URLEncoder.encode(uri.getPath(), StandardCharsets.UTF_8.toString()) : null;
            String query = uri.getQuery() != null ? URLEncoder.encode(uri.getQuery(), StandardCharsets.UTF_8.toString()) : null;
            new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), path, query, uri.getFragment()).toURL();
            return true;
        } catch (URISyntaxException | IOException e) {
            logger.log(Level.WARNING, "Invalid URL: " + url, e);
            return false;
        }
    }


    public void shutdown() {
        executor.shutdown();
        logger.info("Executor service shut down");
    }
}
