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
package ai.yda.framework.rag.core.model;

import java.util.Map;

import lombok.Getter;

/**
 * Represents a document's content and associated metadata.
 * Used for processing and chunking documents into smaller parts.
 *
 * @author Bogdan Synenko
 * @since 0.2.0
 */
@Getter
public class DocumentData {

    /**
     * The content of the document.
     */
    private final String content;

    /**
     * The metadata associated with the document, such as document identifiers or other properties.
     */
    private final Map<String, Object> metadata;

    /**
     * Constructs a new {@link DocumentData} instance.
     *
     * @param content  the content of the document.
     * @param metadata the metadata associated with the document.
     */
    public DocumentData(final String content, final Map<String, Object> metadata) {
        this.content = content;
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "DocumentData{" + "content='" + content + '\'' + ", metadata=" + metadata + '}';
    }
}
