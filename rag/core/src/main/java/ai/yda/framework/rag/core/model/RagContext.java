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

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents the context data associated with a particular user query. It encapsulates both the knowledge, which is
 * typically a list of strings retrieved from external sources, and metadata, which contains additional key-value pairs
 * providing context or other relevant information.
 *
 * @author Dmitry Marchuk
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@Getter
@Builder(toBuilder = true)
public class RagContext {

    /**
     * A list of strings representing the retrieved knowledge or information relevant to the user's query.
     */
    private final List<String> knowledge;

    /**
     * A map containing metadata related to the context. The keys are strings, and the values can be of any object type,
     * allowing for flexible storage of various types of supplementary information.
     */
    private final Map<String, Object> metadata;

    /**
     * Constructs a new {@link RagContext} instance with the specified knowledge and metadata.
     *
     * @param knowledge the list of strings representing the retrieved knowledge or information relevant to the user's
     *                  query.
     * @param metadata  the map containing metadata related to the context.
     */
    public RagContext(final List<String> knowledge, final Map<String, Object> metadata) {
        this.knowledge = knowledge;
        this.metadata = metadata;
    }
}
