package org.vector.assistant.persistance.dao;

import java.util.UUID;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.persistance.entity.InformationNodeEntity;
import org.vector.assistant.persistance.repository.InformationNodeRepository;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class InformationNodeDao {

    private final InformationNodeRepository informationNodeRepository;

    public Mono<InformationNodeEntity> createInformationNode(String name, String description, UUID userId) {

        InformationNodeEntity entityToSave = InformationNodeEntity.builder()
                .name(name)
                .description(description)
                .userId(userId)
                .isNew(Boolean.TRUE)
                .build();
        return informationNodeRepository.save(entityToSave);
    }

    public Mono<Void> deleteInformationNode(String name, String description, UUID userId) {
        return informationNodeRepository.deleteByNameAndUserId(name, userId);
    }

    public Mono<InformationNodeEntity> getInformationNodeByNameAndUserEmail(String name, UUID userId) {
        return informationNodeRepository.findByNameAndUserId(name, userId);
    }

    public Mono<Boolean> existByNameAndUserId(String name, UUID userId) {
        return informationNodeRepository.existsByNameAndUserId(name, userId);
    }
}
