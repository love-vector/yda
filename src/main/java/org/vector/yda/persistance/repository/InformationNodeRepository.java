package org.vector.yda.persistance.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.vector.yda.persistance.entity.InformationNodeEntity;

@Repository
public interface InformationNodeRepository extends JpaRepository<InformationNodeEntity, Long> {

    List<InformationNodeEntity> findAllByUserId(final UUID userId);
}
