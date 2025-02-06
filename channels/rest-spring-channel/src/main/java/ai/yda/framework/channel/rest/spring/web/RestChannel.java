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
public class RestChannel implements Channel<Query> {

    private final Assistant assistant;

    public RestChannel(final Assistant assistant) {
        this.assistant = assistant;
    }

    @Override
    @PostMapping
    public Query processRequest(@RequestBody @Validated final Query request) {
        return assistant.assist(request);
    }
}
