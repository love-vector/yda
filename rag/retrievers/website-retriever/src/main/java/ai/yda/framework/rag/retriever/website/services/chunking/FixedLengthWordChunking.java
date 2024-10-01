package ai.yda.framework.rag.retriever.website.services.chunking;

import ai.yda.framework.rag.core.retriever.ChunkStrategy;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

public class FixedLengthWordChunking implements ChunkStrategy {
    private final int chunkSize;
    public FixedLengthWordChunking(final @NonNull int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public List<String> splitChunks(final String text) {
        String[] words = text.split("\\s+");
        List<String> chunks = new ArrayList<>();

        for (int i = 0; i < words.length; i += chunkSize) {
            StringBuilder chunk = new StringBuilder();
            for (int j = i; j < i + chunkSize && j < words.length; j++) {
                chunk.append(words[j]).append(" ");
            }
            chunks.add(chunk.toString().trim());
        }

        return chunks;
    }
}
