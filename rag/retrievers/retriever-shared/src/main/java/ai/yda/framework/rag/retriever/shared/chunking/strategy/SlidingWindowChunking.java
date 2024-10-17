/*
 * YDA - Open-Source Java AI Assistant.
 * Copyright (C) 2024 Love Vector OÜ <https://vector-inc.dev/>

 * This file is part of YDA.

 * YDA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * YDA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with YDA.  If not, see <https://www.gnu.org/licenses/>.
*/
package ai.yda.framework.rag.retriever.shared.chunking.strategy;

import java.util.ArrayList;
import java.util.List;

import ai.yda.framework.rag.core.model.Chunk;
import ai.yda.framework.rag.core.model.DocumentData;

/**
 * A chunking strategy that uses a sliding window mechanism to chunk document content into overlapping chunks.
 * Each chunk overlaps with the previous one, which allows for better context preservation in certain tasks.
 *
 * @author Bogdan Synenko
 * @since 0.2.0
 */
public class SlidingWindowChunking implements ChunkStrategy {

    /**
     * The size of the sliding window, determining how many words each chunk contains.
     */
    private final int windowSize;

    /**
     * The step size, determining how far the window moves with each new chunk.
     */
    private final int step;

    /**
     * Constructs a new {@link SlidingWindowChunking} instance with the specified window size and step.
     *
     * @param windowSize the number of words in each chunk.
     * @param step       the number of words to move the window between chunks.
     */
    public SlidingWindowChunking(final int windowSize, final int step) {
        this.windowSize = windowSize;
        this.step = step;
    }

    /**
     * Splits the provided list of documents into overlapping chunks based on a sliding window mechanism.
     *
     * @param documentDataList the list of documents to be chunked.
     * @return a list of chunks, each containing a portion of the document content.
     */
    @Override
    public List<Chunk> splitChunks(final List<DocumentData> documentDataList) {
        List<Chunk> chunks = new ArrayList<>();
        final int[] chunkIndex = {0};

        documentDataList.forEach(documentData -> {
            var text = documentData.getContent();
            var documentId = documentData.getMetadata().get("documentId").toString();
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
