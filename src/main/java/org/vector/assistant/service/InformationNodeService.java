package org.vector.assistant.service;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.exception.information.node.InformationNodeAlreadyExistsException;
import org.vector.assistant.exception.information.node.InformationNodeCannotBeFoundException;
import org.vector.assistant.persistance.dao.InformationNodeDao;
import org.vector.assistant.persistance.dao.MilvusDao;
import org.vector.assistant.persistance.entity.InformationNodeEntity;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InformationNodeService {

    private final InformationNodeDao informationNodeDao;

    private final MilvusDao milvusDao;

    public Mono<URI> createInformationNode(String name, String description, UUID userId) {
        return informationNodeDao.existByNameAndUserId(name, userId).flatMap(exists -> {
            if (exists) {
                return Mono.error(new InformationNodeAlreadyExistsException());
            }

            return informationNodeDao
                    .createInformationNode(name, description, userId)
                    .doOnNext(entity -> {
                        milvusDao.createCollection(name);
                    })
                    .map(informationNode -> URI.create(
                            Objects.requireNonNull(informationNode.getId()).toString()));
        });
    }

    public Mono<ResponseEntity<InformationNodeEntity>> getInformationNode(String name, UUID userId) {
        return informationNodeDao.existByNameAndUserId(name, userId).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new InformationNodeCannotBeFoundException());
            }
            return informationNodeDao
                    .getInformationNodeByNameAndUserId(name, userId)
                    .map(ResponseEntity::ok);
        });
    }

    public Mono<URI> deleteInformationNode(String name, UUID userId) {

        return informationNodeDao.existByNameAndUserId(name, userId).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new InformationNodeCannotBeFoundException());
            }

            Mono<InformationNodeEntity> informationNodeEntityMono =
                    informationNodeDao.getInformationNodeByNameAndUserId(name, userId);
            return informationNodeDao
                    .deleteInformationNode(name, userId)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnSuccess(entity -> {
                        milvusDao.deleteCollectionByName(informationNodeEntityMono
                                .map(InformationNodeEntity::getCollectionName)
                                .block());
                    })
                    .then(Mono.fromCallable(() -> URI.create("Deleted")));
        });
    }

    public Mono<ResponseEntity<URI>> updateInformationNode(
            String name, UUID userId, String updatedName, String updateDescription) {

        return informationNodeDao.existByNameAndUserId(name, userId).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new InformationNodeCannotBeFoundException());
            }

            return informationNodeDao
                    .updateNameWhereNameAndUserId(name, userId, updatedName, updateDescription)
                    .map(informationNode -> URI.create(
                            Objects.requireNonNull(informationNode.getId()).toString()))
                    .then(Mono.just(ResponseEntity.ok(URI.create("Updated"))));
        });
    }
}
