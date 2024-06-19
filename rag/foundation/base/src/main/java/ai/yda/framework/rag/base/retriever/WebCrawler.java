package ai.yda.framework.rag.base.retriever;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class WebCrawler {
    private Set<String> links;
    private static final int MAX_DEPTH = 2;

    public WebCrawler() {
        links = new HashSet<>();
    }

    public void getPageLinks(String URL, int depth) {
        if ((!links.contains(URL) && (depth < MAX_DEPTH))) {
            try {
                links.add(URL);

                Document document = Jsoup.connect(URL).get();
                Elements linksOnPage = document.select("a[href]");

                depth++;
                for (Element page : linksOnPage) {
                    getPageLinks(page.attr("abs:href"), depth);
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }

    public void getDataFromPage(String URL) {
        try {
            Document document = Jsoup.connect(URL).get();
            log.info(document.text());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
