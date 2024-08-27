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
 * Represents an Assistant that processes a Request and returns a Response in a streaming manner.
 * <p>
 * This class is useful when Responses need to be generated progressively, such as when dealing with large amounts of
 * data or when the Response is expected to be produced in chunks.
 * </p>
 *
 * @param <REQUEST>  the generic type of the Request from the User.
 * @param <RESPONSE> the generic type of the Response that will be generated based on the given Request.
 * @author Nikita Litvinov
 * @see Assistant
 * @see StreamingRagAssistant
 * @since 0.1.0
 */
public interface StreamingAssistant<REQUEST, RESPONSE> {

    /**
     * Processes the given Request and returns a corresponding Response in a streaming manner.
     *
     * @param request the Request to be processed.
     * @return a {@link Flux stream} of Response objects generated from processing the Request.
     */
    Flux<RESPONSE> streamAssistance(REQUEST request);
}
