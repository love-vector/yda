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
package ai.yda.framework.channel.rest.spring.streaming.web;

import reactor.core.publisher.Flux;

import org.springframework.ai.rag.Query;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.yda.framework.channel.core.StreamingChannel;
import ai.yda.framework.channel.rest.spring.streaming.RestSpringStreamingProperties;
import ai.yda.framework.core.assistant.StreamingAssistant;

/**
 * Provides REST controller logic that handles incoming requests for processing using a streaming assistant. The path
 * of the endpoint is configurable via properties, allowing for flexibility in deployment.
 *
 * @author Nikita Litvinov
 * @see StreamingAssistant
 * @since 0.1.0
 */
@RestController
@RequestMapping(
        path = "${" + RestSpringStreamingProperties.CONFIG_PREFIX + ".endpoint-relative-path:"
                + RestSpringStreamingProperties.DEFAULT_ENDPOINT_RELATIVE_PATH + "}")
public class RestStreamingChannel implements StreamingChannel<Query> {

    private final StreamingAssistant assistant;

    public RestStreamingChannel(final StreamingAssistant assistant) {
        this.assistant = assistant;
    }

    @Override
    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Query> processRequest(@RequestBody @Validated final Query request) {
        return assistant.streamAssistance(request);
    }
}
