package ai.yda.framework.rag.retriever.filesystem.chunking;

import java.util.List;

import ai.yda.framework.rag.core.model.Chunk;
import org.springframework.ai.document.Document;



public interface ChunkStrategy {
    List<Chunk> splitChunks(List<Document> documents);
}
