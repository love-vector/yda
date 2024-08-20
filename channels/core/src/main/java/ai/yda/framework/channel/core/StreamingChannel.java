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
package ai.yda.framework.channel.core;

import reactor.core.publisher.Flux;

/**
 * Provides a generic interface for implementing communication gateways to the streaming Assistant.
 *
 * @param <REQUEST>  the generic type of the request from the user.
 * @param <RESPONSE> the generic type of the response that will be generated based on the given request.
 * @author Nikita Litvinov
 * @see Channel
 * @since 0.1.0
 */
public interface StreamingChannel<REQUEST, RESPONSE> {

    /**
     * Processes request data involving the streaming assistant.
     *
     * @param request the request object to be processed.
     * @return a {@link Flux stream} of response objects generated after processing the request.
     */
    Flux<RESPONSE> processRequest(REQUEST request);
}
