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
package ai.yda.framework.rag.core.retriever;

import java.util.List;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;

/**
 * Provides a generic mechanism for fetching relevant data or documents that can provide Context based on the User
 * Request.
 *
 * @param <REQUEST> the generic type of the Request from the User, which must extend {@link RagRequest}.
 * @param <CONTEXT> the generic type of the Context data that will be retrieved based on the given Request, which must
 *                  extend {@link RagContext}.
 * @author Nikita Litvinov
 * @see RagRequest
 * @see RagContext
 * @since 0.1.0
 */
public interface Retriever<REQUEST extends RagRequest, CONTEXT extends RagContext> {

    /**
     * Fetches relevant data or documents that can provide additional information based on the User Request.
     *
     * @param request the Request object that contains query data from the User.
     * @return the Context object generated that contains additional information based on the User Request.
     */
    List<CONTEXT> retrieve(REQUEST request);
}
