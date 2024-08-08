package ai.yda.framework.rag.retriever.website;

import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.retriever.website.service.WebsiteService;

@Slf4j
public class WebsiteRetriever implements Retriever<RagRequest, RagContext> {
    private static final int TOP_K = 5;

    private final VectorStore vectorStore;
    private final WebsiteService websiteService = new WebsiteService();
    private final String url;

    public WebsiteRetriever(final VectorStore vectorStore, final String url, final boolean isCrawlingEnabled) {
        this.vectorStore = vectorStore;
        this.url = url;
        if (isCrawlingEnabled) {
            processWebsite();
        }
    }

    @Override
    public RagContext retrieve(final RagRequest request) {
        return RagContext.builder()
                .knowledge(
                        vectorStore
                                .similaritySearch(
                                        SearchRequest.query(request.getQuery()).withTopK(TOP_K))
                                .stream()
                                .map(Document::getContent)
                                .collect(Collectors.toList()))
                .build();
    }

    private void processWebsite() {
        var pageDocuments = websiteService.createChunkDocumentsFromSitemapUrl(url);
        vectorStore.add(pageDocuments);
    }
}
