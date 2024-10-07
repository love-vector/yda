package ai.yda.framework.rag.retriever.website.chunking;

import java.util.List;

import org.springframework.ai.document.Document;

import ai.yda.framework.rag.core.model.Chunk;

public interface ChunkStrategy {
    List<Chunk> splitChunks(List<Document> documents);
}
