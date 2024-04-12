package org.vector.assistant.service;

import java.net.URI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.dto.information.node.*;
import org.vector.assistant.persistance.dao.InformationNodeDao;
import org.vector.assistant.persistance.dao.MilvusDao;
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

    public Mono<InformationNodeDto> getInformationNode(final Long informationNodeId) {

        return informationNodeDao.getInformationNodeById(informationNodeId).map(informationNodeMapper::toDto);
    }

    public Mono<URI> createInformationNode(final InformationNodeDto informationNodeDto) {

        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext ->
                        (UserEntity) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    var informationNode = informationNodeMapper.toEntity(informationNodeDto, user.getId());
                    return informationNodeDao.createInformationNode(informationNode);
                })
                .map(informationNode -> URI.create(informationNode.getId().toString()));
    }

    public Mono<URI> updateInformationNode(final InformationNodeDto request, final Long informationNodeId) {

        return informationNodeDao.getInformationNodeById(informationNodeId).flatMap(informationNodeEntity -> {
            informationNodeEntity.setName(request.name());
            informationNodeEntity.setDescription(request.description());
            return informationNodeDao
                    .updateInformationNode(informationNodeEntity)
                    .publishOn(Schedulers.boundedElastic())
                    .map(informationNode -> URI.create(informationNode.getId().toString()));
        });
    }

    public Mono<Void> deleteInformationNode(final Long informationNodeId) {

        return informationNodeDao
                .getInformationNodeById(informationNodeId)
                .flatMap(informationNodeDao::deleteInformationNode);
    }
}
