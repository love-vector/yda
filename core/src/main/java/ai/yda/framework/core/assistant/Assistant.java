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
 * Represents an Assistant that helps process Requests by utilizing the Retrieval-Augmented Generation (RAG) system.
 * <p>
 * This Assistant plays a central role in managing interactions, transmitting information through communication channels
 * and performing various tasks to assist in the processing of Requests. While it primarily leverages RAG, it is
 * designed to handle additional tasks and functionalities.
 * </p>
 *
 * @param <QUERY>  the generic type of the Request from the User.
 * @param <RESPONSE> the generic type of the Response that will be generated based on the given Request.
 * @author Nikita Litvinov
 * @since 0.1.0
 */
public interface Assistant<QUERY, RESPONSE> {

    /**
     * Processes the given Request and returns a corresponding Response.
     *
     * @param request the Request to be processed.
     * @return the Response generated from processing the Request.
     */
    RESPONSE assist(QUERY request);
}
