package ai.yda.framework.rag.retriever.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ai.yda.framework.rag.core.retriever.util.ContentUtil;
import ai.yda.framework.rag.retriever.filesystem.service.WebsiteCrawlerService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;

@Slf4j
public class WebSiteRetriever implements Retriever<BaseAssistantRequest, BaseRagContext> {

    private final Path localDirectoryPath;
    private final VectorStore vectorStore;
    private final WebsiteCrawlerService websiteCrawlerService = new WebsiteCrawlerService();

    public WebSiteRetriever(String localDirectoryPath, VectorStore vectorStore) {
        this.localDirectoryPath = Paths.get(localDirectoryPath);
        this.vectorStore = vectorStore;

        try {
            init();
        } catch (IOException e) {
            log.error("Failed initialize Retriever: {}", e.getClass());
            throw new RuntimeException(e);
        }
    }

    @Override
    public BaseRagContext retrieve(BaseAssistantRequest request) {
        return BaseRagContext.builder()
                .knowledge(vectorStore
                        .similaritySearch(
                                SearchRequest.query(request.getContent()).withTopK(5))
                        .stream()
                        .map(Document::getContent)
                        .collect(Collectors.toList()))
                .build();
    }

    public void init() throws IOException {
        processWebPages();
    }

    /**
     * Processes web pages by crawling, extracting content and adding it to the vector store.
     *
     * This method initiates web crawling, extracts the content from each page, and then adds
     * this content to the vector store in parallel.
     */
    private void processWebPages() {
       /* Set<String> urlsToCrawl = Set.of("https://example.com"); // Замените на реальные URLs
        Set<String> allLinks = new HashSet<>();

        for (String url : urlsToCrawl) {
            allLinks.addAll(websiteCrawlerService.getPageLinks(url, 0));
        }

        allLinks.parallelStream().forEach(url -> {
            Document document = websiteCrawlerService.getDataFromPage(url);
            String content = websiteCrawlerService.extractContent(document);
            var documentChunks = ContentUtil.splitContent(content, 1000);
            var documents = documentChunks.parallelStream().map(Document::new).collect(Collectors.toList());
            vectorStore.add(documents);
        });
        return; */
    }
}
