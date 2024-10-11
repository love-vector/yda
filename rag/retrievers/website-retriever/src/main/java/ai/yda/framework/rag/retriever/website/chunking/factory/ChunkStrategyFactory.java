package ai.yda.framework.rag.retriever.website.chunking.factory;

import ai.yda.framework.rag.core.retriever.ChunkStrategy;
import ai.yda.framework.rag.retriever.website.chunking.FixedLengthWordChunking;
import ai.yda.framework.rag.retriever.website.chunking.RegexChunking;

import java.util.List;

public class ChunkStrategyFactory {
    public ChunkStrategy getStrategy(ChunkingAlgorithm chunkingAlgorithm) {
        switch (chunkingAlgorithm) {
            case FIXED -> {
                return new FixedLengthWordChunking(1000);
            }
            case REGEX -> {
                return new RegexChunking(List.of("[.!?]\\s+"));
            }
            default -> throw new RuntimeException();
        }
    }
}
