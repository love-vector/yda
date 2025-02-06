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

import reactor.core.publisher.Flux;

import org.springframework.ai.rag.Query;

import ai.yda.framework.rag.core.StreamingRag;
import ai.yda.framework.rag.core.model.RagResponse;

/**
 * Represents a RAG Assistant that processes a {@link Query} and returns a {@link Flux} of {@link RagResponse}
 * in a streaming manner.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
public class StreamingRagAssistant implements StreamingAssistant<Query, RagResponse> {

    /**
     * The {@link StreamingRag} instance responsible for asynchronous RAG processing.
     */
    private final StreamingRag<Query, RagResponse> streamingRag;

    /**
     * Constructs a new {@link StreamingRagAssistant} instance.
     *
     * @param streamingRag the {@link StreamingRag} instance used for streaming request-response processing.
     */
    public StreamingRagAssistant(final StreamingRag<Query, RagResponse> streamingRag) {
        this.streamingRag = streamingRag;
    }

    /**
     * Processes the given {@link Query} by delegating to the {@link StreamingRag#streamRag(Query)} method
     * and returns a {@link Flux} of {@link RagResponse}.
     *
     * @param query the {@link Query} to be processed.
     * @return a {@link Flux} stream of {@link RagResponse} objects.
     */
    @Override
    public Flux<RagResponse> streamAssistance(final Query query) {
        return streamingRag.streamRag(query);
    }
}
