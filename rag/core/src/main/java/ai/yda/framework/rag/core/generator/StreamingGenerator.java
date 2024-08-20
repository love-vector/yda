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
package ai.yda.framework.rag.core.generator;

import reactor.core.publisher.Flux;

import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

/**
 * Provides a generic mechanism that takes the user's query and the retrieved context to produce a final response,
 * often by leveraging a language model or other generative mechanism.
 * <p>
 * This interface allow to generate responses in a streaming manner and is useful when responses need to be generated
 * progressively, such as when dealing with large amounts of data or when the response is expected to be produced in
 * chunks.
 * </p>
 *
 * @param <REQUEST>  the generic type of the request from the user, which must extend {@link RagRequest}.
 * @param <RESPONSE> the generic type of the response generated based on the given request, which must extend
 *                   {@link RagResponse}.
 * @author Nikita Litvinov
 * @see RagRequest
 * @see RagResponse
 * @see Generator
 * @since 0.1.0
 */
public interface StreamingGenerator<REQUEST extends RagRequest, RESPONSE extends RagResponse> {

    /**
     * Streams responses based on the provided request and context. The method returns a Flux that emits a sequence of
     * responses, allowing for the processing of data in a non-blocking and incremental manner.
     *
     * @param request the request object that contains query data from the user.
     * @param context the context object that helps to better understand or interpret a request or question that a user
     *                provides.
     * @return a {@link Flux stream} of responses generated as a result of processing the request and context. Each
     * response in the Flux represents a part of the overall response, allowing for the incremental delivery of results.
     */
    Flux<RESPONSE> streamGeneration(REQUEST request, String context);
}
