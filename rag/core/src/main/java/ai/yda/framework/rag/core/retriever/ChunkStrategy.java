package ai.yda.framework.rag.core.retriever;

import java.util.List;

import ai.yda.framework.rag.core.retriever.entity.Chunk;
import ai.yda.framework.rag.core.retriever.entity.DocumentData;

public interface ChunkStrategy {
    List<Chunk> splitChunks(List<DocumentData> documents);
}
