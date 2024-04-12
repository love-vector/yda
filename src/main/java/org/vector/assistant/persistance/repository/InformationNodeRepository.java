package org.vector.assistant.persistance.repository;

import java.util.UUID;

import reactor.core.publisher.Flux;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import org.vector.assistant.persistance.entity.InformationNodeEntity;

@Repository
public interface InformationNodeRepository extends R2dbcRepository<InformationNodeEntity, Long> {

    Flux<InformationNodeEntity> findAllByUserId(final UUID userId);
}
