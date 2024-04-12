package org.vector.assistant.persistance.dao;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vector.assistant.exception.notfound.AssistantNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.persistance.entity.AssistantEntity;
import org.vector.assistant.persistance.repository.AssistantRepository;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class AssistantDao {

    private final AssistantRepository assistantRepository;

    public Mono<Boolean> existsByNameAndUserId(final String email, final UUID userId) {
        return assistantRepository.existsByNameAndUserId(email, userId);
    }

    public Mono<Boolean> existsByIdAndUserId(final String assistantId, final UUID userId) {
        return assistantRepository.existsByIdAndUserId(assistantId, userId);
    }

    public Mono<AssistantEntity> createAssistant(final AssistantEntity assistant) {
        assistant.setIsNew(true);
        return assistantRepository.save(assistant);
    }

    public Mono<AssistantEntity> updateAssistant(final AssistantEntity assistant) {
        return assistantRepository.save(assistant);
    }

    public Mono<AssistantEntity> getAssistant(final String name, final UUID userId) {
        return assistantRepository.findByIdAndUserId(name, userId).switchIfEmpty(Mono.error(AssistantNotFoundException::new));
    }

    public Flux<AssistantEntity> getAssistantsForCurrentUser(final UUID userId) {
        return assistantRepository.findAllByUserId(userId);
    }

    public Mono<Void> deleteAssistant(final String id) {
        return assistantRepository.deleteById(id);
    }
}
