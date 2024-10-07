package ai.yda.framework.rag.retriever.filesystem.chunking;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;

import ai.yda.framework.rag.core.model.Chunk;

public class FixedLengthWordChunking implements ChunkStrategy {
    private final int chunkSize;

    public FixedLengthWordChunking(final int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public List<Chunk> splitChunks(final List<Document> documents) {
        List<Chunk> chunks = new ArrayList<>();
        final int[] chunkIndex = {0};

        documents.forEach(document -> {
            var text = document.getContent();
            var documentId = document.getMetadata().get("documentId").toString();
            var words = text.split("\\s+");

            for (int i = 0; i < words.length; i += chunkSize) {
                StringBuilder chunkText = new StringBuilder();
                for (int j = i; j < i + chunkSize && j < words.length; j++) {
                    chunkText.append(words[j]).append(" ");
                }

                var chunk = new Chunk(chunkText.toString().trim(), chunkIndex[0]++, documentId);
                chunks.add(chunk);
            }
        });
        return chunks;
    }
}
