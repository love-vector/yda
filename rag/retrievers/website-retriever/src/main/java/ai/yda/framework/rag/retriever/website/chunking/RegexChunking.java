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
import java.util.Arrays;
import java.util.List;

import org.springframework.ai.document.Document;

import ai.yda.framework.rag.core.model.Chunk;

public class RegexChunking implements ChunkStrategy {
    private final List<String> patterns;

    public RegexChunking(final List<String> patterns) {
        if (patterns.isEmpty()) {
            this.patterns = List.of("\\n\\n");
        } else {
            this.patterns = patterns;
        }
    }

    @Override
    public List<Chunk> splitChunks(final List<Document> documents) {
        List<Chunk> chunks = new ArrayList<>();
        final int[] chunkIndex = {0};

        documents.forEach(document -> {
            var text = document.getContent();
            var documentId = document.getMetadata().get("documentId").toString();

            List<String> paragraphs = new ArrayList<>();
            paragraphs.add(text);

            for (String pattern : patterns) {
                List<String> newParagraphs = new ArrayList<>();
                for (String paragraph : paragraphs) {
                    var splitParagraphs = paragraph.split(pattern);
                    newParagraphs.addAll(Arrays.asList(splitParagraphs));
                }
                paragraphs = newParagraphs;
            }
            paragraphs.forEach(paragraph -> chunks.add(new Chunk(paragraph.trim(), chunkIndex[0]++, documentId)));
        });

        return chunks;
    }
}
