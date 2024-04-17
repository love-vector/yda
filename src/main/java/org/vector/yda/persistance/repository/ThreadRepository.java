package org.vector.yda.persistance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.vector.yda.persistance.entity.ThreadEntity;

@Repository
public interface ThreadRepository extends JpaRepository<ThreadEntity, Long> {

    List<ThreadEntity> findAllByAssistantId(final Long assistantId);
}
