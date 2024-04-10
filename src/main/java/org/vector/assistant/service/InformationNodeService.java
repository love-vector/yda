package org.vector.assistant.service;

import java.net.URI;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.dto.information.node.*;
import org.vector.assistant.exception.information.node.InformationNodeDoesNotExistsException;
import org.vector.assistant.persistance.dao.InformationNodeDao;
import org.vector.assistant.persistance.dao.MilvusDao;
import org.vector.assistant.persistance.entity.InformationNodeEntity;
import org.vector.assistant.persistance.entity.UserEntity;
import org.vector.assistant.util.mapper.InformationNodeMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InformationNodeService {

    private final InformationNodeDao informationNodeDao;

    private final MilvusDao milvusDao;

    private final InformationNodeMapper informationNodeMapper;

    public Mono<URI> createInformationNode(final InformationNodeDto informationNodeDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext ->
                        (UserEntity) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    var informationNode = informationNodeMapper.toEntity(informationNodeDto, user.getId());
                    return informationNodeDao.createInformationNode(informationNode);
                })
                .flatMap(informationNode -> {
                    milvusDao.createCollection(informationNode.getCollectionName());
                    return Mono.just(URI.create(informationNode.getId().toString()));
                });
    }

    public Mono<ResponseEntity<InformationNodeDto>> getInformationNode(UUID informationNodeId) {

        return informationNodeDao.existsById(informationNodeId).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new InformationNodeDoesNotExistsException());
            }
            return informationNodeDao.getInformationNodeById(informationNodeId).map(entity -> {
                InformationNodeDto dto = InformationNodeDto.builder()
                        .name(entity.getName())
                        .description(entity.getDescription())
                        .build();
                return ResponseEntity.ok(dto);
            });
        });
    }

    public Mono<ResponseEntity<URI>> deleteInformationNode(UUID informationNodeId) {

        return informationNodeDao.existsById(informationNodeId).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new InformationNodeDoesNotExistsException());
            }

            Mono<InformationNodeEntity> informationNodeEntityMono =
                    informationNodeDao.getInformationNodeById(informationNodeId);

            return informationNodeDao
                    .deleteInformationNode(informationNodeId)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnSuccess(entity -> {
                        milvusDao.deleteCollectionByName(informationNodeEntityMono
                                .map(InformationNodeEntity::getCollectionName)
                                .block());
                    })
                    // TODO return 204
                    .then(Mono.just(ResponseEntity.ok(URI.create("Deleted"))));
        });
    }

    // TODO return resource to entity
    public Mono<ResponseEntity<URI>> updateInformationNode(InformationNodeDto request) {

        InformationNodeDto updatedInformationNodeDto = InformationNodeDto.builder()
                .name(request.updatedName())
                .description(request.updatedDescription())
                .build();

        return informationNodeDao
                .getInformationNodeById(request.informationNodeId())
                .flatMap(informationNodeEntity -> informationNodeDao
                        .updateInformationNode(informationNodeEntity)
                        .publishOn(Schedulers.boundedElastic())
                        .then(Mono.just(ResponseEntity.ok(URI.create("Updated")))))
                .onErrorResume(
                        InformationNodeDoesNotExistsException.class,
                        ex -> Mono.just(ResponseEntity.notFound()
                                .header("Error-Message", ex.getMessage())
                                .location(URI.create(""))
                                .build()));
    }
}
