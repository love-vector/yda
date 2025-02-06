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
package ai.yda.framework.rag.generator.assistant.openai;

import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.ai.rag.Query;

import ai.yda.framework.rag.core.generator.StreamingGenerator;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.generator.assistant.openai.service.AzureOpenAiAssistantService;
import ai.yda.framework.rag.generator.assistant.openai.util.OpenAiAssistantConstant;
import ai.yda.framework.session.core.ReactiveSessionProvider;

/**
 * Generates responses to the Request in a streaming manner by sending queries to the Assistant Service. The class
 * relies on the {@link AzureOpenAiAssistantService} for communicating with the Assistant and uses a {@code assistantId}
 * field to identify the Assistant being used.
 *
 * @author Iryna Kopchak
 * @author Nikita Litvinov
 * @see AzureOpenAiAssistantService
 * @since 0.1.0
 */
@Slf4j
public class OpenAiAssistantStreamingGenerator implements StreamingGenerator<Query, RagResponse> {

    /**
     * Service used to interact with the Azure OpenAI Assistant API.
     */
    private final AzureOpenAiAssistantService assistantService;

    /**
     * The reactive provider responsible for managing Session data.
     */
    private final ReactiveSessionProvider reactiveSessionProvider;

    /**
     * The ID of the Assistant to be used.
     */
    private final String assistantId;

    /**
     * Constructs a new {@link OpenAiAssistantStreamingGenerator} instance.
     *
     * @param assistantService        the {@link AzureOpenAiAssistantService} instance used to interact with the
     *                                Azure OpenAI Service.
     * @param assistantId             the unique identifier for the Assistant that will be used to interact with the
     *                                Azure OpenAI Service. This ID specifies which Assistant to use when making
     *                                requests.
     * @param reactiveSessionProvider the {@link ReactiveSessionProvider} instance responsible for managing sessions
     *                                in a reactive manner, maintaining user context between interactions.
     */
    public OpenAiAssistantStreamingGenerator(
            final AzureOpenAiAssistantService assistantService,
            final ReactiveSessionProvider reactiveSessionProvider,
            final String assistantId) {
        this.assistantService = assistantService;
        this.reactiveSessionProvider = reactiveSessionProvider;
        this.assistantId = assistantId;
    }

    /**
     * Generates a Response for a given Request using the OpenAI Assistant Service in a streaming manner. This involves
     * either retrieving an existing Thread ID from the Reactive Session Provider or creating a new Thread, sending the
     * Request query to the Assistant, and obtaining the Response as a stream.
     *
     * @param request the {@link Query} object containing the query from the User.
     * @return a {@link Flux} stream of {@link RagResponse} objects containing the result of the Assistant's Response.
     */
    @Override
    public Flux<RagResponse> streamGeneration(final Query request) {
        var context = request.context().values().stream().map(Object::toString).collect(Collectors.joining(" ,"));
        return reactiveSessionProvider
                .get(OpenAiAssistantConstant.THREAD_ID_KEY)
                .map(Object::toString)
                .flatMap(threadId -> Mono.fromCallable(() -> assistantService
                                .addMessageToThread(threadId, request.text())
                                .getThreadId())
                        .subscribeOn(Schedulers.boundedElastic()))
                .switchIfEmpty(Mono.defer(() -> Mono.fromCallable(() ->
                                assistantService.createThread(request.text()).getId())
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(threadId -> reactiveSessionProvider
                                .put(OpenAiAssistantConstant.THREAD_ID_KEY, threadId)
                                .thenReturn(threadId))))
                .doOnNext(threadId -> log.debug("Thread ID: {}", threadId))
                .flatMapMany(threadId -> assistantService.createRunStream(threadId, assistantId, context))
                .map(deltaMessage -> RagResponse.builder().result(deltaMessage).build());
    }
}
