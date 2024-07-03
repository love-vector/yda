package ai.yda.framework.rag.retriever.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.retriever.filesystem.service.DocumentService;

@Slf4j
public class FilesystemRetriever implements Retriever<BaseAssistantRequest, BaseRagContext> {

    private final Path localDirectoryPath;
    private final VectorStore vectorStore;
    private final DocumentService documentService = new DocumentService();

    public FilesystemRetriever(String localDirectoryPath, VectorStore vectorStore) {
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
                                SearchRequest.query(request.getQuery()).withTopK(5))
                        .stream()
                        .map(Document::getContent)
                        .collect(Collectors.toList()))
                .build();
    }

    public void init() throws IOException {
        processFolder();
        moveFilesToProcessed();
    }

    /**
     * Processes all files in the local directory by creating document chunks and adding them to the vector store.
     * <p>
     * This method lists all regular files in the local directory, processes each file to create chunks,
     * and then adds these chunks to the vector store in parallel. If the directory is empty, it logs
     * an informational message and returns without processing.
     * </p>
     *
     * @throws IOException if an I/O error occurs when accessing the directory or files.
     */
    private void processFolder() throws IOException {

        try (Stream<Path> paths = Files.list(localDirectoryPath)) {
            var fileList = paths.filter(Files::isRegularFile).toList();

            if (fileList.isEmpty()) {
                log.info("No files to process in directory: {}", localDirectoryPath);
                return;
            }

            fileList.parallelStream()
                    .map(Path::toString)
                    .map(documentService::createDocumentChunks)
                    .map(chunkList ->
                            chunkList.parallelStream().map(Document::new).collect(Collectors.toList()))
                    .forEach(vectorStore::add);
        }
    }

    /**
     * Moves all files from the local directory to the "processed" folder.
     * <p>
     * This method checks if the "processed" folder exists in the parent directory of the local directory,
     * creates it if it does not exist, and then moves all files from the local directory to the "processed" folder.
     * </p>
     *
     * @throws IOException if an I/O error occurs when accessing or moving the files.
     */
    private void moveFilesToProcessed() throws IOException {
        Path processedDir = localDirectoryPath.resolveSibling("processed");

        if (!Files.exists(processedDir)) {
            Files.createDirectory(processedDir);
        }

        try (Stream<Path> files = Files.list(localDirectoryPath)) {
            files.filter(Files::isRegularFile).forEach(file -> {
                try {
                    Files.move(file, processedDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    log.error("Failed to move file {} to processed directory: {}", file, e);
                }
            });
        }
    }
}
