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
package ai.yda.framework.rag.core.retriever.chunking.model;

import lombok.Getter;

/**
 * Represents a chunk of data extracted from a source.
 *
 * @author Bogdan Synenko
 * @since 0.2.0
 */
@Getter
public class Chunk {

    /**
     * The textual content of the chunk.
     */
    private final String text;

    /**
     * The index of this chunk within the original document.
     */
    private final int index;

    /**
     * The identifier of the document from which this chunk was extracted.
     */
    private final String documentId;

    /**
     * Constructs a new {@link Chunk} instance.
     *
     * @param text       the text content of the chunk.
     * @param index      the index of the chunk in the original document.
     * @param documentId the ID of the document from which the chunk is extracted.
     */
    public Chunk(final String text, final int index, final String documentId) {
        this.text = text;
        this.index = index;
        this.documentId = documentId;
    }

    @Override
    public String toString() {
        return "Chunk{" + "text='" + text + '\'' + ", index=" + index + ", documentId='" + documentId + '\'' + '}';
    }
}
