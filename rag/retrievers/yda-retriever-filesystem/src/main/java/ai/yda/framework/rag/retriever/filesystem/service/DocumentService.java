package ai.yda.framework.rag.retriever.filesystem.service;

import java.io.File;
import java.util.List;

import ai.yda.framework.rag.core.retriever.util.ContentUtil;
import ai.yda.framework.rag.retriever.filesystem.util.FileUtil;

public class DocumentService {
    private static final Integer CHUNK_MAX_LENGTH = 1000;

    public List<String> createDocumentChunks(final String filePath) {

        var pdfContent = FileUtil.readPdf(new File(filePath));
        var preprocessedContent = ContentUtil.preprocessContent(pdfContent);
        return ContentUtil.splitContent(preprocessedContent, CHUNK_MAX_LENGTH);
    }
}
