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
package ai.yda.framework.rag.core.retriever.chunking.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.yda.framework.rag.core.retriever.chunking.entity.Chunk;
import ai.yda.framework.rag.core.retriever.chunking.entity.DocumentData;

/**
 * A chunking strategy that splits document content based on regular expression patterns.
 * This strategy is useful for chunking documents into sentences or other logical divisions.
 *
 * @author Bogdan Synenko
 * @since 0.2.0
 */
public class RegexChunking implements ChunkStrategy {

    /**
     * A list of compiled regular expression patterns used to split the document content.
     */
    private final List<Pattern> patterns;

    /**
     * Constructs a new {@link RegexChunking} instance with the specified list of regex patterns.
     *
     * @param patterns a list of regular expressions to be used for splitting the document content.
     */
    public RegexChunking(final List<String> patterns) {
        this.patterns = new ArrayList<>();
        patterns.parallelStream().forEach(regex -> this.patterns.add(Pattern.compile(regex)));
    }

    /**
     * Splits the provided list of documents into chunks based on the specified regular expressions.
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

            patterns.forEach(pattern -> {
                Matcher matcher = pattern.matcher(text);
                int lastMatchEnd = 0;

                while (matcher.find()) {
                    String chunkText =
                            text.substring(lastMatchEnd, matcher.start()).trim();
                    if (!chunkText.isEmpty()) {
                        chunks.add(new Chunk(chunkText, chunkIndex[0]++, documentId));
                    }
                    lastMatchEnd = matcher.end();
                }

                if (lastMatchEnd < text.length()) {
                    chunks.add(new Chunk(text.substring(lastMatchEnd).trim(), chunkIndex[0]++, documentId));
                }
            });
        });

        return chunks;
    }
}
