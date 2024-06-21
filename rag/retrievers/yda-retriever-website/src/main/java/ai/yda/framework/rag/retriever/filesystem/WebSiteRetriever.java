package ai.yda.framework.rag.retriever.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ai.yda.framework.rag.retriever.filesystem.service.WebsiteService;
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
    private final WebsiteService websiteCrawlerService = new WebsiteService();

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
        try (Stream<Path> paths = Files.list(localDirectoryPath)) {
            var fileList = paths.filter(Files::isRegularFile).toList();

            if (fileList.isEmpty()) {
                log.info("No files to process in directory: {}", localDirectoryPath);
                return;
            }

            fileList.parallelStream()
                    .map(Path::toString)
                    .map(chunkList ->
                            chunkList.map(Document::new).collect(Collectors.toList()))
                    .forEach(vectorStore::add);
        }
    }
}
