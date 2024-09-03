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

import ai.yda.framework.rag.core.StreamingRag;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

/**
 * Represents a streaming RAG Assistant that processes a Request and returns a Response in a streaming manner. This
 * class delegates the Request processing to the {@link  StreamingRag} instance provided via constructor injection.
 * <p>
 * This class is useful when Responses need to be generated progressively, such as when dealing with large amounts of
 * data or when the Response is expected to be produced in chunks.
 * </p>
 *
 * @author Nikita Litvinov
 * @see StreamingRag
 * @see RagAssistant
 * @since 0.1.0
 */
public class StreamingRagAssistant implements StreamingAssistant<RagRequest, RagResponse> {
    /**
     * The {@link StreamingRag} instance responsible for processing the {@link RagRequest} and generating the
     * {@link RagResponse} in streaming manner.
     */
    private final StreamingRag<RagRequest, RagResponse> rag;

    /**
     * Constructs a new {@link StreamingRagAssistant} instance with the specified {@link StreamingRag} instance.
     *
     * @param rag the {@link StreamingRag} instance used to handle {@link RagRequest} and {@link RagResponse}
     *            processing. This parameter cannot be {@code null}.
     */
    public StreamingRagAssistant(final StreamingRag<RagRequest, RagResponse> rag) {
        this.rag = rag;
    }

    @Override
    public Flux<RagResponse> streamAssistance(final RagRequest request) {
        return rag.streamRag(request);
    }
}
