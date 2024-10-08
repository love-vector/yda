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

import ai.yda.framework.rag.core.Rag;
import ai.yda.framework.rag.core.StreamingRag;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

/**
 * Represents a RAG Assistant that processes a {@link RagRequest} and returns a {@link RagResponse}.
 * This class delegates the request processing to the provided {@link Rag} or {@link StreamingRag} instances,
 * depending on whether synchronous or streaming processing is required.
 *
 * <p>If both {@link Rag} and {@link StreamingRag} are provided, the class supports both regular and streaming
 * operations. At least one must be supplied during construction.</p>
 *
 * @author Nikita Litvinov
 * @see Rag
 * @see StreamingRag
 * @since 0.1.0
 */
public class RagAssistant implements Assistant<RagRequest, RagResponse>, StreamingAssistant<RagRequest, RagResponse> {

    /**
     * The {@link Rag} instance responsible for synchronous processing of {@link RagRequest} and generating
     * the {@link RagResponse}.
     */
    private final Rag<RagRequest, RagResponse> rag;

    /**
     * The {@link StreamingRag} instance responsible for streaming processing of {@link RagRequest} and generating
     * the {@link RagResponse} in a reactive manner.
     */
    private final StreamingRag<RagRequest, RagResponse> streamingRag;

    /**
     * Constructs a new {@link RagAssistant} instance with the specified {@link Rag} and {@link StreamingRag} instances.
     *
     * @param rag          the {@link Rag} instance used for synchronous request-response processing.
     * @param streamingRag the {@link StreamingRag} instance used for streaming request-response processing.
     * @throws IllegalArgumentException if both {@link Rag} and {@link StreamingRag} are null.
     */
    public RagAssistant(
            final Rag<RagRequest, RagResponse> rag, final StreamingRag<RagRequest, RagResponse> streamingRag) {
        if (rag == null && streamingRag == null) {
            throw new IllegalArgumentException("At least one of Rag or StreamingRag must be provided.");
        }
        this.rag = rag;
        this.streamingRag = streamingRag;
    }

    /**
     * Processes the given {@link RagRequest} synchronously by delegating to the {@link Rag#doRag(RagRequest)} method.
     *
     * @param request the {@link RagRequest} to be processed.
     * @return the {@link RagResponse} generated from processing the request.
     * @throws IllegalStateException if the {@link Rag} instance is not available.
     */
    @Override
    public RagResponse assist(final RagRequest request) {
        if (rag == null) {
            throw new IllegalStateException("Rag is required to use this method.");
        }
        return rag.doRag(request);
    }

    /**
     * Processes the given {@link RagRequest} in a streaming manner by delegating to the
     * {@link StreamingRag#streamRag(RagRequest)} method.
     *
     * @param request the {@link RagRequest} to be processed.
     * @return a {@link Flux} emitting {@link RagResponse} objects in real-time as the request is processed.
     * @throws IllegalStateException if the {@link StreamingRag} instance is not available.
     */
    @Override
    public Flux<RagResponse> streamAssistance(final RagRequest request) {
        if (streamingRag == null) {
            throw new IllegalStateException("StreamingRag is required to use this method.");
        }
        return streamingRag.streamRag(request);
    }
}
