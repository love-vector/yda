package ai.yda.framework.rag.core.retriever;

import java.util.List;

public interface ChunkStrategy {
    public List<String> splitChunks(String text);
}
