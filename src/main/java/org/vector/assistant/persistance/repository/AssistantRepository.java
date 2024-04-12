package org.vector.assistant.persistance.repository;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import org.vector.assistant.persistance.entity.AssistantEntity;

public interface AssistantRepository extends ReactiveCrudRepository<AssistantEntity, String> {

    @Query("SELECT COUNT(*) > 0 FROM chatbot.assistant WHERE name = :name AND user_id = :userId")
    Mono<Boolean> existsByNameAndUserId(String name, UUID userId);

    Mono<Boolean> existsByIdAndUserId(String assistantId, UUID userId);

    Flux<AssistantEntity> findAllByUserId(UUID userId);

    Mono<AssistantEntity> findByIdAndUserId(final String name, final UUID userId);
}
