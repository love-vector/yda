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
 * Represents a RAG Assistant that processes a Request and returns a Response. This class delegates the Request
 * processing to the {@link Rag} instance provided via constructor injection.
 *
 * @author Nikita Litvinov
 * @see Rag
 * @see StreamingRagAssistant
 * @since 0.1.0
 */
public class RagAssistant implements Assistant<RagRequest, RagResponse> {

    /**
     * The {@link Rag} instance responsible for processing the {@link RagRequest} and generating the
     * {@link RagResponse}.
     */
    private final Rag<RagRequest, RagResponse> rag;

    /**
     * Constructs a new {@link RagAssistant} instance with the specified {@link Rag} instance.
     *
     * @param rag the {@link Rag} instance used to handle {@link RagRequest} and {@link RagResponse} processing.
     */
    public RagAssistant(final Rag<RagRequest, RagResponse> rag) {
        this.rag = rag;
    }

    /**
     * Processes the given {@link RagRequest} by delegating to the {@link Rag#doRag(RagRequest)} method and returns the
     * resulting {@link RagResponse}.
     *
     * @param request the {@link RagRequest} to be processed.
     * @return the {@link RagResponse} generated from processing the request.
     */
    @Override
    public RagResponse assist(final RagRequest request) {
        return rag.doRag(request);
    }
}
