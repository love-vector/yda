package ai.yda.framework.rag.core.retriever;

import ai.yda.framework.rag.core.model.Chunk;
import ai.yda.framework.rag.core.model.CrawlResult;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

public interface Indexer {
    List<Document> index(List<CrawlResult> chunks);

    List<Chunk> process(List<CrawlResult> chunks);

    void save(List<Document> documents);

}