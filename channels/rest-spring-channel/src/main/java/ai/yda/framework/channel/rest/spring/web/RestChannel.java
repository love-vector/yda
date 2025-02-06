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
package ai.yda.framework.channel.rest.spring.web;

import org.springframework.ai.rag.Query;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.yda.framework.channel.core.Channel;
import ai.yda.framework.channel.rest.spring.RestSpringProperties;
import ai.yda.framework.core.assistant.Assistant;
import ai.yda.framework.rag.core.model.RagResponse;

/**
 * Provides REST controller logic that handles incoming requests for processing using an assistant. The path of the
 * endpoint is configurable via properties, allowing for flexibility in deployment.
 *
 * @author Nikita Litvinov
 * @see Assistant
 * @since 0.1.0
 */
@RestController
@RequestMapping(
        path = "${" + RestSpringProperties.CONFIG_PREFIX + ".endpoint-relative-path:"
                + RestSpringProperties.DEFAULT_ENDPOINT_RELATIVE_PATH + "}")
public class RestChannel implements Channel<RagResponse, Query> {

    private final Assistant<RagResponse, Query> assistant;

    /**
     * Constructs a new {@link RestChannel} instance with the specified {@link Assistant} instance.
     *
     * @param assistant the {@link Assistant} instance used to process {@link Query} and generate
     *                  {@link RagResponse}.
     */
    public RestChannel(final Assistant<RagResponse, Query> assistant) {
        this.assistant = assistant;
    }

    /**
     * Processes the incoming {@link Query} by delegating it to the {@link Assistant}.
     *
     * <p>
     * This method is mapped to the HTTP POST method and handles the logic for processing a validated
     * {@link Query}. The request is passed to the assistant, which performs the necessary operations
     * to generate a {@link RagResponse}.
     * </p>
     *
     * @param request the {@link Query} object containing the data to be processed.
     * @return the {@link RagResponse} object generated by the assistant.
     */
    @Override
    @PostMapping
    public RagResponse processRequest(@RequestBody @Validated final Query request) {
        return assistant.assist(request);
    }
}
