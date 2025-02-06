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

public class RagAssistant implements Assistant<RagResponse, Query> {

    /**
     * The {@link Rag} instance responsible for synchronous RAG processing.
     */
    private final Rag<RagResponse, Query> rag;

    /**
     * Constructs a new {@link RagAssistant} instance.
     *
     * @param rag the {@link Rag} instance used for synchronous request-response processing.
     */
    public RagAssistant(final Rag<RagResponse, Query> rag) {
        this.rag = rag;
    }

    @Override
    public RagResponse assist(final Query request) {
        return rag.doRag(request);
    }
}
