package ai.yda.framework.rag.core.retriever.chunking;

import java.util.List;

public interface ChunkStrategy {
    public List<String> splitChunks(String text);
}
