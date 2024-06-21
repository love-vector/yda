package ai.yda.framework.rag.retriever.filesystem.service;

import ai.yda.framework.rag.retriever.filesystem.constants.Constants;
import ai.yda.framework.rag.retriever.filesystem.exception.WebsiteReadException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WebsiteService {
    private Set<String> links;

    public WebsiteService() {
        links = new HashSet<>();
    }

    public Set<String> getPageLinks(String url, int depth) {
        if ((!links.contains(url) && (depth < Constants.MAX_DEPTH))) {
            try {
                links.add(url);
                Document document = Jsoup.connect(url).get();
                Elements linksOnPage = document.select("a[href]");

                depth++;
                for (Element page : linksOnPage) {
                    String absHref = page.attr("abs:href");
                    if (isValidURL(absHref)) {
                        try {
                            getPageLinks(absHref, depth);
                        } catch (WebsiteReadException e) {
                            System.err.println("Skipping url due to error: " + absHref);
                        }
                    }
                }
            } catch (IOException e) {
                throw new WebsiteReadException(e);
            }
        }
        return links;
    }

    public List<Document> getPageDocuments(String url, int depth) {
        var linker = getPageLinks(url, depth);
        return linker.stream().map(this::safeConnect).collect(Collectors.toList());
    }

    private Document safeConnect(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (HttpStatusException e) {
            System.err.println("HTTP error fetching URL. Status=" + e.getStatusCode() + ", URL=" + url);
            return null;
        } catch (IOException e) {
            throw new WebsiteReadException(e);
        }
    }

    private boolean isValidURL(String url) {
        try {
            new URI(url).toURL();
            return true;
        } catch (URISyntaxException | IOException e) {
            return false;
        }
    }
}