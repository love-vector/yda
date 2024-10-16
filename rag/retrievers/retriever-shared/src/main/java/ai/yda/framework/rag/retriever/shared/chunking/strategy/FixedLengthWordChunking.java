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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.yda.framework.rag.core.model.Chunk;
import ai.yda.framework.rag.core.model.DocumentData;

/**
 * A chunking strategy that splits document content into fixed-length chunks based on word count.
 * Each chunk is limited to a certain number of characters.
 *
 * @author Bogdan Synenko
 * @since 0.2.0
 */
public class FixedLengthWordChunking implements ChunkStrategy {

    /**
     * The maximum number of characters allowed per chunk.
     */
    private final int chunkSize;

    /**
     * Constructs a new {@link FixedLengthWordChunking} instance with the specified chunk size.
     *
     * @param chunkSize the maximum number of characters for each chunk.
     */
    public FixedLengthWordChunking(final int chunkSize) {
        this.chunkSize = chunkSize;
    }

    /**
     * Splits the provided list of documents into chunks of fixed length.
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
