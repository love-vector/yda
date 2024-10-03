package ai.yda.framework.rag.core.retriever.chunking;

import ai.yda.framework.rag.core.model.Chunk;
import org.springframework.ai.document.Document;

import java.util.ArrayList;
import java.util.List;

public class SlidingWindowChunking implements ChunkStrategy {
    private final int windowSize;
    private final int step;

    public SlidingWindowChunking(final int windowSize, final int step) {
        this.windowSize = windowSize;
        this.step = step;
    }

    @Override
    public List<Chunk> splitChunks(final List<Document> documents) {
        List<Chunk> chunks = new ArrayList<>();
        final int[] chunkIndex = {0};
        documents.forEach(document -> {
            var text = document.getContent();
            var documentId = document.getMetadata().get("documentId").toString();
            document.getMetadata();

            String[] words = text.split("\\s+");

            for (int i = 0; i < words.length; i += step) {
                StringBuilder chunkText = new StringBuilder();
                for (int j = i; j < i + windowSize && j < words.length; j++) {
                    chunkText.append(words[j]).append(" ");
                }
                Chunk chunk = new Chunk(chunkText.toString().trim(), chunkIndex[0]++, documentId);
                chunks.add(chunk);
            }
        });
        return chunks;
    }
}
