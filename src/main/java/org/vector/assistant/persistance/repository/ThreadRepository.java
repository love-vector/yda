package org.vector.assistant.persistance.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import org.vector.assistant.persistance.entity.ThreadEntity;

public interface ThreadRepository extends ReactiveCrudRepository<ThreadEntity, String> {}
