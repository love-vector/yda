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
package ai.yda.framework.rag.generator.assistant.openai.util;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.util.StreamingRequestTransformer;
import ai.yda.framework.rag.generator.assistant.openai.service.AzureOpenAiAssistantService;
import ai.yda.framework.session.core.ReactiveSessionProvider;

/**
 * Resolves and enhances the {@link RagRequest} by adding context from conversation threads in a streaming manner.
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
public class StreamingContextResolver implements StreamingRequestTransformer<RagRequest> {

    /**
     * Service used to interact with the Azure OpenAI Assistant API.
     */
    private final AzureOpenAiAssistantService assistantService;

    /**
     * The provider responsible for managing Session data in a reactive manner.
     */
    private final ReactiveSessionProvider reactiveSessionProvider;

    /**
     * The ID of the assistant used for resolving context within the request.
     */
    private final String contextResolverAssistantId;

    /**
     * Constructs a new {@link StreamingContextResolver} instance.
     *
     * @param assistantService           the service used to interact with the Azure OpenAI Assistant API.
     * @param reactiveSessionProvider    the session provider used to manage session data in a reactive manner.
     * @param contextResolverAssistantId the ID of the assistant used for resolving context in requests.
     */
    public StreamingContextResolver(
            final AzureOpenAiAssistantService assistantService,
            final ReactiveSessionProvider reactiveSessionProvider,
            final String contextResolverAssistantId) {
        this.assistantService = assistantService;
        this.reactiveSessionProvider = reactiveSessionProvider;
        this.contextResolverAssistantId = contextResolverAssistantId;
    }

    /**
     * Enhances the given {@link RagRequest} by incorporating additional context based on the conversation history,
     * resolving it in a reactive manner.
     *
     * @param request the original {@link RagRequest} to be enhanced.
     * @return a {@link Mono} emitting the updated {@link RagRequest} with refined query data, or the original request
     * if no context is found.
     */
    @Override
    public Mono<RagRequest> transformRequest(final RagRequest request) {
        return reactiveSessionProvider
                .get(OpenAiAssistantConstant.THREAD_ID_KEY)
                .map(Object::toString)
                .flatMap(threadId -> Mono.fromRunnable(
                                () -> assistantService.addMessageToThread(threadId, request.getQuery()))
                        .subscribeOn(Schedulers.boundedElastic())
                        .thenReturn(threadId))
                .flatMap(threadId -> Mono.fromCallable(() ->
                                assistantService.createRunAndWaitForResponse(threadId, contextResolverAssistantId))
                        .subscribeOn(Schedulers.boundedElastic())
                        .<RagRequest>map(responseQuery ->
                                request.toBuilder().query(responseQuery).build()))
                .defaultIfEmpty(request);
    }
}
