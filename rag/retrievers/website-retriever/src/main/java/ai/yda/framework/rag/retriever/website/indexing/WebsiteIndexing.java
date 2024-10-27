package ai.yda.framework.rag.retriever.website.indexing;

import ai.yda.framework.rag.core.indexing.Index;
import ai.yda.framework.rag.core.model.DocumentData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

@Slf4j
public class WebsiteIndexing implements Index<DocumentData> {
    private final VectorStore vectorStore;

    public WebsiteIndexing(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void saveDocuments(List<DocumentData> documentDataList) {
        var documents = documentDataList.parallelStream().map(documentData -> new Document(documentData.getContent(), documentData.getMetadata())).toList();
        vectorStore.add(documents);
    }
}