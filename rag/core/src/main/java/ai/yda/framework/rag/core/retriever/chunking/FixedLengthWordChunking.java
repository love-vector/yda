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
package ai.yda.framework.rag.core.retriever.chunking;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.yda.framework.rag.core.retriever.chunking.entity.Chunk;
import ai.yda.framework.rag.core.retriever.chunking.entity.DocumentData;

public class FixedLengthWordChunking implements ChunkStrategy {
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

            Pattern pattern = Pattern.compile(".{1," + chunkSize + "}");
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                var chunkText = matcher.group().trim();
                chunks.add(new Chunk(chunkText, chunkIndex[0]++, documentId));
            }
        });
        return chunks;
    }
}
