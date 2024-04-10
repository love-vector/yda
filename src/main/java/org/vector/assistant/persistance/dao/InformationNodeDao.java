package org.vector.assistant.persistance.dao;

import java.util.UUID;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.exception.information.node.InformationNodeDoesNotExistsException;
import org.vector.assistant.persistance.entity.InformationNodeEntity;
import org.vector.assistant.persistance.repository.InformationNodeRepository;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class InformationNodeDao {

    private final InformationNodeRepository informationNodeRepository;

    public Mono<InformationNodeEntity> createInformationNode(final InformationNodeEntity informationNode) {
        return informationNodeRepository.save(
                informationNode.toBuilder().isNew(Boolean.TRUE).build());
    }

    public Mono<Void> deleteInformationNode(final UUID informationNodeUUID) {
        return informationNodeRepository.deleteById(informationNodeUUID);
    }

    public Mono<InformationNodeEntity> getInformationNodeById(final UUID informationNodeId) {
        return informationNodeRepository
                .findById(informationNodeId)
                .switchIfEmpty(Mono.error(InformationNodeDoesNotExistsException::new));
    }

    public Mono<Boolean> existsById(final UUID informationNodeId) {
        return informationNodeRepository.existsById(informationNodeId);
    }

    public Mono<InformationNodeEntity> updateInformationNode(final InformationNodeEntity informationNode) {
        return informationNodeRepository.save(
                informationNode.toBuilder().isNew(Boolean.FALSE).build());
    }
}
