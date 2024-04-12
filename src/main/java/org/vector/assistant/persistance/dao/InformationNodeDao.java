package org.vector.assistant.persistance.dao;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
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

    private final InformationNodeRepository informationNodeRepository;

    private final MilvusDao milvusDao;

    public Mono<InformationNodeEntity> getInformationNodeById(final Long informationNodeId) {

        return informationNodeRepository
                .findById(informationNodeId)
                .switchIfEmpty(Mono.error(InformationNodeDoesNotExistsException::new));
    }

    public Mono<InformationNodeEntity> createInformationNode(final InformationNodeEntity informationNode) {

        return informationNodeRepository
                .save(informationNode.toBuilder().build())
                .flatMap(savedNode -> Mono.fromRunnable(() -> milvusDao.createCollection(savedNode.getCollectionName()))
                        .subscribeOn(Schedulers.boundedElastic())
                        .thenReturn(savedNode));
    }

    public Mono<InformationNodeEntity> updateInformationNode(final InformationNodeEntity informationNode) {
        return informationNodeRepository.save(informationNode.toBuilder().build());
    }

    public Mono<Void> deleteInformationNode(final InformationNodeEntity informationNode) {
        return informationNodeRepository
                .deleteById(informationNode.getId())
                .doOnSuccess(entity -> milvusDao.deleteCollectionByName(informationNode.getCollectionName()))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
