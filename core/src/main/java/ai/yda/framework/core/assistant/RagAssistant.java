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
package ai.yda.framework.core.assistant;

import org.springframework.ai.rag.Query;

import ai.yda.framework.rag.core.Rag;
import ai.yda.framework.rag.core.model.RagResponse;

/**
 * Represents a RAG Assistant that synchronously processes a {@link Query} and returns a corresponding
 * {@link RagResponse}.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
public class RagAssistant implements Assistant<Query, RagResponse> {

    /**
     * The {@link Rag} instance responsible for synchronous RAG processing.
     */
    private final Rag<Query, RagResponse> rag;

    /**
     * Constructs a new {@link RagAssistant} instance.
     *
     * @param rag the {@link Rag} instance used for synchronous request-response processing.
     */
    public RagAssistant(final Rag<Query, RagResponse> rag) {
        this.rag = rag;
    }

    /**
     * Processes the given {@link Query} synchronously by delegating to the {@link Rag#doRag(Query)} method.
     *
     * @param query the {@link Query} to be processed.
     * @return the {@link RagResponse} generated from processing the request.
     */
    @Override
    public RagResponse assist(final Query query) {
        return rag.doRag(query);
    }
}
