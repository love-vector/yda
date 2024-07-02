package ai.yda.framework.rag.retriever.website;

import java.io.IOException;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.retriever.website.service.WebsiteService;

@Slf4j
public class WebsiteRetriever implements Retriever<BaseAssistantRequest, BaseRagContext> {
    private final VectorStore vectorStore;
    private final WebsiteService websiteService = new WebsiteService();

    private final String url;

    public WebsiteRetriever(VectorStore vectorStore, String url, boolean isCrawlingEnabled) {
        this.vectorStore = vectorStore;
        this.url = url;
        try {
            if (isCrawlingEnabled) {
                init();
            }
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
        processWebsite();
    }

    private void processWebsite() {
        websiteService.getPageDocuments(url).values().parallelStream()
                .map(documentChunks ->
                        documentChunks.parallelStream().map(Document::new).collect(Collectors.toList()))
                .toList()
                .forEach(vectorStore::add);
    }
}
