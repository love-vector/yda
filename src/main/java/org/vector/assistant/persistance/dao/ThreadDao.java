package org.vector.assistant.persistance.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.persistance.entity.ThreadEntity;
import org.vector.assistant.persistance.repository.ThreadRepository;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class ThreadDao {

    private final ThreadRepository threadRepository;

    public Mono<ThreadEntity> createThread(final ThreadEntity thread) {
        thread.setIsNew(true);
        return threadRepository.save(thread);
    }

    public Flux<ThreadEntity> findAll() {
        return threadRepository.findAll();
    }
}
