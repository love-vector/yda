package ai.yda.framework.rag.retriever.filesystem.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ai.yda.framework.rag.retriever.filesystem.exception.WebsiteReadException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebsiteCrawlerService {
    private Set<String> links;
    private static final int MAX_DEPTH = 2;

    public WebsiteCrawlerService() {
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

    public Document getDataFromPage(String URL) {
        try {
            return Jsoup.connect(URL).get();
        } catch (IOException e) {
            throw new WebsiteReadException(e);
        }
    }

    public String extractContent(Document document) {
        StringBuilder sb = new StringBuilder();

        String textContent = document.body().text();
        sb.append("Text Content: ").append(textContent).append("\n");

        document.select("h1, h2, h3, h4, h5, h6").forEach(
                heading -> sb.append("Heading: ").append(heading.text()).append("\n")
        );

        Elements headings = document.select("h1, h2, h3, h4, h5, h6");
        for (Element heading : headings) {
            sb.append("Heading: ").append(heading.text()).append("\n");
        }

        Elements links = document.select("a[href]");
        for (Element link : links) {
            sb.append("Link: ").append(link.attr("abs:href")).append(" Text: ").append(link.text()).append("\n");
        }

        Elements paragraphs = document.select("p");
        for (Element paragraph : paragraphs) {
            sb.append("Paragraph: ").append(paragraph.text()).append("\n");
        }

        return sb.toString();
    }
}
