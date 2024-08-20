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

/**
 * Represents an assistant that processes user requests and provides appropriate responses.
 *
 * @param <REQUEST>  the generic type of the request from the user.
 * @param <RESPONSE> the generic type of the response that will be generated based on the given request.
 * @author Nikita Litvinov
 * @see StreamingAssistant
 * @see RagAssistant
 * @since 0.1.0
 */
public interface Assistant<REQUEST, RESPONSE> {

    /**
     * Processes the given request and returns a corresponding response.
     *
     * @param request the request to be processed.
     * @return the response generated from processing the request.
     */
    RESPONSE assist(REQUEST request);
}
