package ai.yda.framework.rag.retriever.filesystem.util;

import ai.yda.framework.rag.retriever.filesystem.service.WebsiteCrawlerService;

public final class WebsiteUtil {

    public void retrieve() {
        var page = "https://www.kpi.kharkov.ua/ukr/ntu-hpi/";
        WebsiteCrawlerService crawler = new WebsiteCrawlerService();
        crawler.getPageLinks(page, 3).forEach(crawler::getDataFromPage);
    }

    private WebsiteUtil() {}
}
