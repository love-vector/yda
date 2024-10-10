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
package ai.yda.framework.rag.retriever.website.chunking;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;

import ai.yda.framework.rag.core.model.Chunk;

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
