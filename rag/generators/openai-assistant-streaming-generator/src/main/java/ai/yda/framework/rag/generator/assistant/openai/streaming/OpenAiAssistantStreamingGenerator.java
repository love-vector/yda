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
package ai.yda.framework.rag.generator.assistant.openai.streaming;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import ai.yda.framework.rag.core.generator.StreamingGenerator;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.generator.shared.AzureOpenAiAssistantService;
import ai.yda.framework.session.core.ReactiveSessionProvider;

/**
 * Generates responses to user queries in a streaming manner by sending queries to the assistant service. The class
 * relies on the {@link AzureOpenAiAssistantService} for communicating with the assistant, and uses a
 * {@code assistantId} field to identify the assistant being used.
 *
 * @author Nikita Litvinov
 * @see AzureOpenAiAssistantService
 * @since 0.1.0
 */
@Slf4j
public class OpenAiAssistantStreamingGenerator implements StreamingGenerator<RagRequest, RagResponse> {

    /**
     * The constant representing the thead id key.
     */
    private static final String THREAD_ID_KEY = "threadId";

    private final AzureOpenAiAssistantService assistantService;

    /**
     * The ID of the assistant to be used.
     */
    private final String assistantId;

    /**
     * The reactive provider responsible for managing session data.
     */
    private final ReactiveSessionProvider sessionProvider;

    /**
     * Constructs a new {@link OpenAiAssistantStreamingGenerator} instance with the specified apiKey, assistantId,
     * sessionProvider.
     *
     * @param apiKey          the API key used to authenticate with the Azure OpenAI service.
     * @param assistantId     the unique identifier for the assistant that will be used to interact with the Azure
     *                        OpenAI service. This ID is required to specify which assistant to use when making
     *                        requests.
     * @param sessionProvider the {@link ReactiveSessionProvider} instance responsible for providing and managing
     *                        sessions. It is used to manage user sessions and maintain context between interactions
     *                        with the assistant.
     */
    public OpenAiAssistantStreamingGenerator(
            final String apiKey, final String assistantId, final ReactiveSessionProvider sessionProvider) {
        this.assistantService = new AzureOpenAiAssistantService(apiKey);
        this.assistantId = assistantId;
        this.sessionProvider = sessionProvider;
    }

    /**
     * Generates a response for a given request using the OpenAI assistant service in a streaming manner. This involves
     * either retrieving an existing thread ID from the session provider or creating a new thread, sending the request
     * query to the assistant, and obtaining the response.
     *
     * @param request the {@link RagRequest} object containing the query from the user.
     * @param context the context to be included in the request to the assistant.
     * @return a {@link Flux stream} of {@link RagResponse} objects containing the result of the assistant's response.
     */
    @Override
    public Flux<RagResponse> streamGeneration(final RagRequest request, final String context) {
        return sessionProvider
                .get(THREAD_ID_KEY)
                .map(Object::toString)
                .flatMap(threadId -> Mono.fromRunnable(
                                () -> assistantService.addMessageToThread(threadId, request.getQuery()))
                        .subscribeOn(Schedulers.boundedElastic())
                        .thenReturn(threadId))
                .switchIfEmpty(Mono.defer(() -> Mono.fromCallable(() -> assistantService
                                        .createThread(request.getQuery())
                                        .getId())
                                .subscribeOn(Schedulers.boundedElastic()))
                        .flatMap(threadId ->
                                sessionProvider.put(THREAD_ID_KEY, threadId).thenReturn(threadId)))
                .doOnNext(threadId -> log.debug("Thread ID: {}", threadId))
                .flatMapMany(threadId -> assistantService.createRunStream(threadId, assistantId, context))
                .map(deltaMessage -> RagResponse.builder().result(deltaMessage).build());
    }
}
