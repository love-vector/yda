package org.vector.assistant.service;

import java.net.URI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.dto.InformationNodeDto;
import org.vector.assistant.persistance.dao.InformationNodeDao;
import org.vector.assistant.persistance.entity.UserEntity;
import org.vector.assistant.util.mapper.InformationNodeMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InformationNodeService {

    private final InformationNodeDao informationNodeDao;

    private final InformationNodeMapper informationNodeMapper;

    public Mono<InformationNodeDto> getInformationNode(final Long informationNodeId) {
        return informationNodeDao.getInformationNodeById(informationNodeId).map(informationNodeMapper::toDto);
    }

    public Flux<InformationNodeDto> getUserInformationNodes() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext ->
                        (UserEntity) securityContext.getAuthentication().getPrincipal())
                .flatMapMany(user -> informationNodeDao.getInformationNodesByUserId(user.getId()))
                .map(informationNodeMapper::toDto);
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

    public Mono<Void> updateInformationNode(final Long informationNodeId, final InformationNodeDto informationNodeDto) {
        return informationNodeDao
                .getInformationNodeById(informationNodeId)
                .flatMap(informationNode -> informationNodeDao.updateInformationNode(
                        informationNodeMapper.updateEntity(informationNode, informationNodeDto)))
                .then();
    }

    public Mono<Void> deleteInformationNode(final Long informationNodeId) {
        return informationNodeDao
                .getInformationNodeById(informationNodeId)
                .flatMap(informationNodeDao::deleteInformationNode);
    }
}
