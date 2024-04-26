package org.vector.yda.persistance.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.vector.yda.persistance.entity.AssistantEntity;

@Repository
public interface AssistantRepository extends JpaRepository<AssistantEntity, Long> {

    List<AssistantEntity> findAllByUserId(final UUID userId);
}
