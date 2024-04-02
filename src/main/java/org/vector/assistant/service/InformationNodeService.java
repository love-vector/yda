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

    /**
     * Retrieves a specific {@link InformationNodeDto} by its ID.
     *
     * @param informationNodeId the ID of the information node to retrieve.
     * @return a {@link Mono} emitting the {@link InformationNodeDto}.
     */
    public Mono<InformationNodeDto> getInformationNode(final Long informationNodeId) {
        return informationNodeDao
                .getInformationNodeById(informationNodeId)
                .map(informationNodeMapper::toDto)
                .doOnSubscribe(subscription -> log.debug("Getting InformationNode with id: {}", informationNodeId))
                .doOnError(error ->
                        log.error("Failed during getting InformationNode with id: {}", informationNodeId, error));
    }

    /**
     * Retrieves all information nodes related to the currently authenticated user.
     *
     * @return a {@link Flux} emitting {@link InformationNodeDto} objects.
     */
    public Flux<InformationNodeDto> getUserInformationNodes() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext ->
                        (UserEntity) securityContext.getAuthentication().getPrincipal())
                .flatMapMany(user -> informationNodeDao.getInformationNodesByUserId(user.getId()))
                .map(informationNodeMapper::toDto)
                .doOnSubscribe(subscription -> log.debug("Getting InformationNodes of authenticated user"))
                .doOnError(
                        error -> log.error("Failed during getting authenticated user's InformationNode list", error));
    }

    /**
     * Creates a new information node based on the provided DTO and returns the URI of the newly created node.
     *
     * @param informationNodeDto the DTO containing the information node data.
     * @return a {@link Mono} emitting the URI of the newly created information node.
     */
    public Mono<URI> createInformationNode(final InformationNodeDto informationNodeDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext ->
                        (UserEntity) securityContext.getAuthentication().getPrincipal())
                .flatMap(user -> {
                    var informationNode = informationNodeMapper.toEntity(informationNodeDto, user.getId());
                    return informationNodeDao.createInformationNode(informationNode);
                })
                .map(informationNode -> URI.create(informationNode.getId().toString()))
                .doOnSubscribe(subscription -> log.debug("Started creating InformationNode for authenticated user"))
                .doOnError(error -> log.error("Failed to create InformationNode for authenticated user", error));
    }

    /**
     * Updates an existing information node identified by its ID with new data provided in the DTO.
     *
     * @param informationNodeId  the ID of the information node to update.
     * @param informationNodeDto the DTO containing updated data for the information node.
     * @return a {@link Mono} signaling completion.
     */
    public Mono<Void> updateInformationNode(final Long informationNodeId, final InformationNodeDto informationNodeDto) {
        return informationNodeDao
                .getInformationNodeById(informationNodeId)
                .flatMap(informationNode -> informationNodeDao.updateInformationNode(
                        informationNodeMapper.updateEntity(informationNode, informationNodeDto)))
                .then()
                .doOnSubscribe(
                        subscription -> log.debug("Started updating InformationNode with id: {}", informationNodeId))
                .doOnError(
                        error -> log.error("Failed to update InformationNode with id: {}", informationNodeId, error));
    }

    /**
     * Deletes an information node identified by its ID.
     *
     * @param informationNodeId the ID of the information node to delete.
     * @return a {@link Mono} signaling completion or error if the deletion fails.
     */
    public Mono<Void> deleteInformationNode(final Long informationNodeId) {
        return informationNodeDao
                .getInformationNodeById(informationNodeId)
                .flatMap(informationNodeDao::deleteInformationNode)
                .doOnSubscribe(
                        subscription -> log.debug("Started deleting InformationNode with id: {}", informationNodeId))
                .doOnError(
                        error -> log.error("Failed to delete InformationNode with id: {}", informationNodeId, error));
    }
}
