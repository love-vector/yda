package ai.yda.framework.rag.retriever.filesystem.service;

import ai.yda.framework.rag.retriever.filesystem.constants.Constants;
import ai.yda.framework.rag.retriever.filesystem.exception.WebsiteReadException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
                            System.out.println("Skipping url due to error: " + absHref);
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
            System.out.println("HTTP error fetching URL. Status=" + e.getStatusCode() + ", URL=" + url);
            return null;
        } catch (IOException e) {
            throw new WebsiteReadException(e);
        }
    }

    public Map<String, String> DocumentFilterData(List<Document> documents) {
        Map<String, String> result = new HashMap<>();
        for (Document doc : documents) {
            if (doc != null) {
                StringBuilder documentContent = new StringBuilder();
                doc.select("h1, h2, h3, h4, h5, h6").forEach(heading -> documentContent.append("Heading: ").append(heading.text()).append("\n"));
                doc.select("a[href]").forEach(link -> documentContent.append("Link: ").append(link.attr("abs:href")).append(" Text: ").append(link.text()).append("\n"));
                doc.select("p").forEach(paragraph -> documentContent.append("Paragraph: ").append(paragraph.text()).append("\n"));
                result.put(doc.location(),documentContent.toString());
            }
        }
        return result;
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