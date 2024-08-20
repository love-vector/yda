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

import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

/**
 * Provides a generic mechanism that coordinates the retrieval, augmentation, and generation processes to produce a
 * final response based on the user's query.
 *
 * @param <REQUEST>  the generic type of the request from the user, which must extend {@link RagRequest}.
 * @param <RESPONSE> the generic type of the response generated based on the given request, which must extend
 *                   {@link RagResponse}.
 * @author Nikita Litvinov
 * @see StreamingRag
 * @see RagRequest
 * @see RagResponse
 * @since 0.1.0
 */
public interface Rag<REQUEST extends RagRequest, RESPONSE extends RagResponse> {

    /**
     * Performs a Retrieval-Augmented Generation (RAG) operation based on the provided request.
     *
     * @param request the request query from the user.
     * @return the response object containing the results of the RAG operation.
     */
    RESPONSE doRag(REQUEST request);
}
