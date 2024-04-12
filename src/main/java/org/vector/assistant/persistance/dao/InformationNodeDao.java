package org.vector.assistant.persistance.dao;

import java.util.UUID;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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

    private final MilvusDao milvusDao;

    private final InformationNodeRepository informationNodeRepository;

    public Mono<InformationNodeEntity> getInformationNodeById(final Long informationNodeId) {
        return informationNodeRepository
                .findById(informationNodeId)
                .switchIfEmpty(Mono.error(InformationNodeDoesNotExistsException::new));
    }

    public Flux<InformationNodeEntity> getInformationNodesByUserId(final UUID userId) {
        return informationNodeRepository.findAllByUserId(userId);
    }

    public Mono<InformationNodeEntity> createInformationNode(final InformationNodeEntity informationNode) {
        return informationNodeRepository.save(informationNode).flatMap(createdNode -> Mono.fromRunnable(
                        () -> milvusDao.createCollectionIfNotExist(createdNode.getCollectionName()))
                .subscribeOn(Schedulers.boundedElastic())
                .thenReturn(createdNode));
    }

    public Mono<InformationNodeEntity> updateInformationNode(final InformationNodeEntity informationNode) {
        return informationNodeRepository.save(informationNode);
    }

    public Mono<Void> deleteInformationNode(final InformationNodeEntity informationNode) {
        return informationNodeRepository
                .delete(informationNode)
                .then(Mono.defer(() -> Mono.fromRunnable(
                                () -> milvusDao.deleteCollectionByName(informationNode.getCollectionName()))
                        .subscribeOn(Schedulers.boundedElastic())))
                .then();
    }
}
