package ai.yda.framework.rag.retriever.website.chunking;

import ai.yda.framework.rag.core.retriever.ChunkStrategy;
import ai.yda.framework.rag.core.retriever.entity.Chunk;
import ai.yda.framework.rag.core.retriever.entity.DocumentData;

import java.util.ArrayList;
import java.util.List;

public class FixedLengthWordChunking implements ChunkStrategy{
    private final int chunkSize;

    public FixedLengthWordChunking(final int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public List<Chunk> splitChunks(final List<DocumentData> documents) {
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
