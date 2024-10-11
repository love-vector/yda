package ai.yda.framework.rag.retriever.website.chunking.factory;

import ai.yda.framework.rag.core.retriever.ChunkStrategy;
import ai.yda.framework.rag.core.retriever.entity.Chunk;
import ai.yda.framework.rag.core.retriever.entity.DocumentData;

import java.util.List;

public class PatternBasedChunking {
    private final ChunkStrategyFactory chunkStrategyFactory;

    public PatternBasedChunking() {
        this.chunkStrategyFactory = new ChunkStrategyFactory();
    }

    public List<Chunk> chunkList(ChunkingAlgorithm chunkingAlgorithm, List<DocumentData> documents) {
        ChunkStrategy strategy = chunkStrategyFactory.getStrategy(chunkingAlgorithm);
        return strategy.splitChunks(documents);
    }
}
