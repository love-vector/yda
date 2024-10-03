package ai.yda.framework.rag.core.retriever.chunking;

import ai.yda.framework.rag.core.model.Chunk;
import org.springframework.ai.document.Document;

import java.util.List;

public interface ChunkStrategy {
    public List<Chunk> splitChunks(List<Document> documents);
}
