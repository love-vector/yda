package org.vector.yda.service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.stereotype.Component;

import org.vector.yda.model.request.ChatRequest;
import org.vector.yda.persistance.dao.AssistantDao;

@Component
@RequiredArgsConstructor
public class ChatService {

    private final ThreadService threadService;
    private final OpenAiService openAiService;

    private final AssistantDao assistantDao;

    public Flux<String> chat(final ChatRequest request) {
        return Mono.fromCallable(() -> assistantDao.getAssistantById(request.assistantId()))
                .flatMapMany(
                        assistant -> Mono.fromCallable(() -> threadService.createOrGetAnyThreadByAssistant(assistant))
                                .flatMap(thread -> Mono.fromCallable(() -> openAiService.addMessageToThread(
                                                thread.getThreadId(), request.message()))
                                        .thenReturn(thread))
                                .flatMapMany(thread ->
                                        openAiService.streamRun(assistant.getAssistantId(), thread.getThreadId())))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
