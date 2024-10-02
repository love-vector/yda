package ai.yda.framework.rag.retriever.filesystem.service.chunking;

import ai.yda.framework.rag.core.retriever.ChunkStrategy;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SlidingWindowChunking implements ChunkStrategy {
    private final int windowSize;
    private final int step;

    public SlidingWindowChunking(final @NonNull int windowSize, final @NonNull int step) {
        this.windowSize = windowSize;
        this.step = step;
    }

    @Override
    public List<String> splitChunks(final String text) {
        String[] words = text.split("\\s+");
        List<String> chunks = new ArrayList<>();

        for (int i = 0; i < words.length; i += step) {
            StringBuilder chunk = new StringBuilder();
            for (int j = i; j < i + windowSize && j < words.length; j++) {
                chunk.append(words[j]).append(" ");
            }
            chunks.add(chunk.toString().trim());
        }
        return chunks;
    }
}
