package org.vector.assistant.persistance.repository;

import java.util.UUID;

import reactor.core.publisher.Mono;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import org.vector.assistant.persistance.entity.IntentionEntity;

@Repository
public interface IntentionRepository extends R2dbcRepository<IntentionEntity, Long> {

    Mono<IntentionEntity> findByVectorId(final UUID vectorId);
}
