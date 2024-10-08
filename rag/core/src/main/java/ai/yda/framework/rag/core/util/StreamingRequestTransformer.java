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
package ai.yda.framework.rag.core.util;

import reactor.core.publisher.Mono;

import ai.yda.framework.rag.core.model.RagRequest;

/**
 * Defines a contract for transforming a {@link REQUEST} in a streaming manner.
 *
 * @param <REQUEST> the generic type of the request that extends {@link RagRequest}.
 * @author Nikita Litvinov
 * @since 0.2.0
 */
public interface StreamingRequestTransformer<REQUEST extends RagRequest> {

    /**
     * Transforms the given {@link REQUEST} and returns the transformed version as a {@link Mono}.
     *
     * @param request the request to transform.
     * @return a {@link Mono} containing the transformed request.
     */
    Mono<REQUEST> transformRequest(REQUEST request);
}
