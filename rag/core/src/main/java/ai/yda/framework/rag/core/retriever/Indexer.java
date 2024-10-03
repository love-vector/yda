package ai.yda.framework.rag.core.retriever;

import org.springframework.ai.document.Document;

import java.util.List;

public interface Indexer {
    void index();

    List<Document> process(List<Document> crawlResult);

    void save(List<Document> documents);

}