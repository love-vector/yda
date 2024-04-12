package org.vector.assistant.service;

import java.util.Collections;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.ThreadRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.stereotype.Component;

import org.vector.assistant.persistance.dao.ThreadDao;
import org.vector.assistant.persistance.entity.ThreadEntity;

@Component
@RequiredArgsConstructor
public class ThreadService {

    private final OpenAiService openAiService;

    private final ThreadDao threadDao;

    public Mono<ThreadEntity> getThread() {
        Mono<ThreadEntity> existingThread = threadDao.findAll().next();
        return existingThread.switchIfEmpty(Mono.fromCallable(() -> openAiService.createThread(ThreadRequest.builder()
                        .messages(Collections.emptyList())
                        .build()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(thread -> ThreadEntity.builder().id(thread.getId()).build())
                .flatMap(threadDao::createThread));
    }
}
