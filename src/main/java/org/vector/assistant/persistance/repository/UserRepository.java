package org.vector.assistant.persistance.repository;

import java.util.UUID;

import reactor.core.publisher.Mono;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import org.vector.assistant.persistance.entity.UserEntity;

@Repository
public interface UserRepository extends R2dbcRepository<UserEntity, UUID> {

    Mono<UserEntity> findByEmail(final String email);

    Mono<Boolean> existsByEmail(final String email);
}
