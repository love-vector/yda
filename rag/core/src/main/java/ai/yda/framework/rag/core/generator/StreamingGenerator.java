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

import org.springframework.ai.rag.Query;

import ai.yda.framework.rag.core.model.RagResponse;

/**
 * Provides a generic mechanism that takes the User's Request and the retrieved Context to produce a final Response,
 * often by leveraging a language model or other generative mechanism.
 * <p>
 * This interface allow to generate responses in a streaming manner and is useful when Responses need to be generated
 * progressively, such as when dealing with large amounts of data or when the Response is expected to be produced in
 * chunks.
 * </p>
 *
 * @param <QUERY>  the generic type of the query from the User, which must extend {@link Query}.
 * @param <RESPONSE> the generic type of the Response generated based on the given Request, which must extend
 *                   {@link RagResponse}.
 * @author Nikita Litvinov
 * @see Query
 * @see RagResponse
 * @see Generator
 * @since 0.1.0
 */
public interface StreamingGenerator<QUERY extends Query, RESPONSE extends RagResponse> {

    /**
     * Streams Responses based on the provided Request and Context. The method returns a Flux that emits a sequence of
     * Responses, allowing for the processing of data in a non-blocking and incremental manner.
     *
     * @param request the Request object that contains query data from the User.
     * @return a {@link Flux stream} of Responses generated as a result of processing the Request and Context. Each
     * response in the Flux represents a part of the overall Response, allowing for the incremental delivery of results.
     */
    Flux<RESPONSE> streamGeneration(QUERY request);
}
