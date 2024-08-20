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

/**
 * Provides a generic mechanism for creating an assistant that processes a request and returns a response in a streaming
 * manner.
 * <p>
 * This class is useful when responses need to be generated progressively, such as when dealing with large amounts of
 * data or when the response is expected to be produced in chunks.
 * </p>
 *
 * @param <REQUEST>  the generic type of the request from the user.
 * @param <RESPONSE> the generic type of the response that will be generated based on the given request.
 * @author Nikita Litvinov
 * @see Assistant
 * @see StreamingRagAssistant
 * @since 0.1.0
 */
public interface StreamingAssistant<REQUEST, RESPONSE> {

    /**
     * Processes the given request and returns a corresponding response in a streaming manner.
     *
     * @param request the request to be processed.
     * @return a {@link Flux stream} of response objects generated from processing the request.
     */
    Flux<RESPONSE> streamAssistance(REQUEST request);
}
