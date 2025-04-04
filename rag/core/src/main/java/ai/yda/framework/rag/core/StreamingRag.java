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
package ai.yda.framework.rag.core;

import reactor.core.publisher.Flux;

import org.springframework.ai.rag.Query;

import ai.yda.framework.rag.core.model.RagResponse;

/**
 * Provides a generic mechanism that coordinates the retrieval, augmentation, and generation processes to produce a
 * final Response based on the Request in a streaming manner.
 *
 * @param <QUERY>  the generic type of the Request, which must extend {@link Query}.
 * @param <RESPONSE> the generic type of the Response generated based on the given Request, which must extend
 *                   {@link RagResponse}.
 * @author Nikita Litvinov
 * @since 0.1.0
 */
public interface StreamingRag<QUERY extends Query, RESPONSE extends RagResponse> {

    /**
     * Performs a Retrieval-Augmented Generation (RAG) operation in a streaming manner based on the provided Request.
     *
     * @param query the Request to process.
     * @return a {@link Flux stream} of Response objects containing the results of the RAG operation.
     */
    Flux<RESPONSE> streamRag(QUERY query);
}
