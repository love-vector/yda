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

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.yda.framework.channel.core.StreamingChannel;
import ai.yda.framework.channel.rest.spring.streaming.RestSpringStreamingProperties;
import ai.yda.framework.core.assistant.StreamingAssistant;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

@RestController
@RequestMapping(
        path = "${" + RestSpringStreamingProperties.CONFIG_PREFIX + ".endpoint-relative-path:"
                + RestSpringStreamingProperties.DEFAULT_ENDPOINT_RELATIVE_PATH + "}")
@RequiredArgsConstructor
public class RestStreamingChannel implements StreamingChannel<RagRequest, RagResponse> {

    private final StreamingAssistant<RagRequest, RagResponse> assistant;

    @Override
    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<RagResponse> processRequest(@RequestBody @Validated final RagRequest ragRequest) {
        return assistant.streamAssistance(ragRequest);
    }
}
