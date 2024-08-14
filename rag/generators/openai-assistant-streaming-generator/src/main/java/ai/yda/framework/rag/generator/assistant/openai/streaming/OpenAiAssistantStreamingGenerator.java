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

@Slf4j
public class OpenAiAssistantStreamingGenerator implements StreamingGenerator<RagRequest, RagResponse> {

    private static final String THREAD_ID_KEY = "threadId";

    private final AzureOpenAiAssistantService assistantService;
    private final String assistantId;
    private final ReactiveSessionProvider sessionProvider;

    public OpenAiAssistantStreamingGenerator(
            final String apiKey, final String assistantId, final ReactiveSessionProvider sessionProvider) {
        this.assistantService = new AzureOpenAiAssistantService(apiKey);
        this.assistantId = assistantId;
        this.sessionProvider = sessionProvider;
    }

    @Override
    public Flux<RagResponse> streamGeneration(final RagRequest request, final String context) {
        return sessionProvider
                .get(THREAD_ID_KEY)
                .map(Object::toString)
                .flatMap(threadId -> Mono.fromRunnable(
                                () -> assistantService.addMessageToThread(threadId, request.getQuery()))
                        .thenReturn(threadId))
                .switchIfEmpty(Mono.defer(() -> Mono.fromCallable(() -> assistantService
                                .createThread(request.getQuery())
                                .getId())
                        .subscribeOn(Schedulers.boundedElastic())))
                .doOnNext(threadId -> log.debug("Thread ID: {}", threadId))
                .flatMapMany(threadId -> assistantService.createRunStream(threadId, assistantId, context))
                .map(deltaMessage -> RagResponse.builder().result(deltaMessage).build());
    }
}
