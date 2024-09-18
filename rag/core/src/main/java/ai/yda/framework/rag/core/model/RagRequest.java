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
package ai.yda.framework.rag.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Represents a Request object that encapsulates the User's query.
 *
 * @author Dmitry Marchuk
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@Getter
@SuperBuilder(toBuilder = true)
public class RagRequest {

    /**
     * The query string provided by the User, which is used as input for the retrieval and generation processes.
     */
    private final String query;

    /**
     * Constructs a new {@link RagRequest} instance with the User's query.
     *
     * @param query the Request query from the User.
     */
    @JsonCreator
    public RagRequest(@JsonProperty("query") final String query) {
        this.query = query;
    }
}
