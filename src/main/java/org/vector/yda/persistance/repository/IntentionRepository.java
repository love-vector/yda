package org.vector.yda.persistance.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.vector.yda.persistance.entity.IntentionEntity;

@Repository
public interface IntentionRepository extends JpaRepository<IntentionEntity, Long> {

    Optional<IntentionEntity> findByVectorId(final UUID vectorId);
}
