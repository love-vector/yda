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

import ai.yda.framework.rag.core.Rag;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

/**
 * Represents a RAG Assistant that synchronously processes a {@link RagRequest} and returns a corresponding
 * {@link RagResponse}.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
public class RagAssistant implements Assistant<RagRequest, RagResponse> {

    /**
     * The {@link Rag} instance responsible for synchronous RAG processing.
     */
    private final Rag<RagRequest, RagResponse> rag;

    /**
     * Constructs a new {@link RagAssistant} instance.
     *
     * @param rag the {@link Rag} instance used for synchronous request-response processing.
     */
    public RagAssistant(final Rag<RagRequest, RagResponse> rag) {
        this.rag = rag;
    }

    /**
     * Processes the given {@link RagRequest} synchronously by delegating to the {@link Rag#doRag(RagRequest)} method.
     *
     * @param request the {@link RagRequest} to be processed.
     * @return the {@link RagResponse} generated from processing the request.
     */
    @Override
    public RagResponse assist(final RagRequest request) {
        return rag.doRag(request);
    }
}
