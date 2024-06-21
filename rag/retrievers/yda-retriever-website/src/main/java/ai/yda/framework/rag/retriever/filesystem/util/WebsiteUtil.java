package ai.yda.framework.rag.retriever.filesystem.util;

import ai.yda.framework.rag.retriever.filesystem.exception.WebsiteReadException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebsiteUtil {
    private Set<String> links;
    private static final int MAX_DEPTH = 2;

    public WebsiteUtil() {
        links = new HashSet<>();
    }

    public Set<String> getPageLinks(String URL, int depth) {
        if ((!links.contains(URL) && (depth < MAX_DEPTH))) {
            try {
                links.add(URL);

                Document document = Jsoup.connect(URL).get();
                Elements linksOnPage = document.select("a[href]");

                depth++;
                for (Element page : linksOnPage) {
                    String absHref = page.attr("abs:href");
                    if (absHref.startsWith("http://") || absHref.startsWith("https://")) {
                        getPageLinks(absHref, depth);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return links;
    }

    public List<Document> getDataFromPage(String URL) {
        try {
            return getPageLinks(URL, 0).stream().map(this::safeConnect).toList();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private Document safeConnect(String URL) {
        try {
            return Jsoup.connect(URL).get();
        } catch (IOException e) {
            throw new WebsiteReadException(e);
        }
    }
}
