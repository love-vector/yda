package ai.yda.framework.rag.retriever.filesystem.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;

import ai.yda.framework.rag.core.util.ContentUtil;
import ai.yda.framework.rag.retriever.filesystem.util.FileUtil;

@Slf4j
public class FilesystemService {
    private static final int CHUNK_MAX_LENGTH = 1000;

    public List<Document> createChunkDocumentsFromFiles(final List<Path> filePathList) {
        return filePathList.parallelStream()
                .map(this::splitFileIntoChunkDocuments)
                .flatMap(List::stream)
                .toList();
    }

    private List<Document> splitFileIntoChunkDocuments(final Path filePath) {
        var pdfContent = FileUtil.readPdf(filePath.toFile());
        var fileName = filePath.getFileName();
        log.debug("Processing file: {}", fileName);
        var preprocessedContent = ContentUtil.preprocessContent(pdfContent);
        return ContentUtil.splitContent(preprocessedContent, CHUNK_MAX_LENGTH).parallelStream()
                .map(documentChunk -> new Document(documentChunk, Map.of("fileName", fileName)))
                .toList();
    }
}
