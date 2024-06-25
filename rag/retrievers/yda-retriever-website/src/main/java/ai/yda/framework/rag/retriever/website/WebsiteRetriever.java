package ai.yda.framework.rag.retriever.filesystem;

import java.io.IOException;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.retriever.filesystem.constants.Constants;
import ai.yda.framework.rag.retriever.filesystem.service.WebsiteService;

@Slf4j
public class WebsiteRetriever implements Retriever<BaseAssistantRequest, BaseRagContext> {
    private final VectorStore vectorStore;
    private final WebsiteService websiteService = new WebsiteService();

    public WebsiteRetriever(VectorStore vectorStore) {
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
        processWebsite();
    }

    private void processWebsite() {
        var documents = websiteService.getPageDocuments(Constants.LINK_FOR_TEST, Constants.DEFAULT_DEPTH);
        var listOfDocuments = websiteService.documentFilterData(documents);
        listOfDocuments.values().parallelStream()
                .map(documentChunks ->
                        documentChunks.parallelStream().map(Document::new).collect(Collectors.toList()))
                .toList()
                .forEach(vectorStore::add);
    }
}
