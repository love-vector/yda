package org.vector.yda.service;

import java.net.URI;
import java.util.List;

import io.milvus.exception.MilvusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.yda.exception.not.found.InformationNodeNotFoundException;
import org.vector.yda.model.dto.InformationNodeDto;
import org.vector.yda.persistance.dao.InformationNodeDao;
import org.vector.yda.security.UserDetailsService;
import org.vector.yda.util.mapper.InformationNodeMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InformationNodeService {

    private final UserDetailsService userDetailsService;

    private final InformationNodeDao informationNodeDao;

    private final InformationNodeMapper informationNodeMapper;

    /**
     * Retrieves a specific {@link InformationNodeDto} by its ID.
     *
     * @param informationNodeId the ID of the information node to retrieve.
     * @return the {@link InformationNodeDto} corresponding to the provided ID.
     * @throws InformationNodeNotFoundException if no information node is found with the provided ID
     */
    public InformationNodeDto getInformationNode(final Long informationNodeId) {
        return informationNodeMapper.toDto(informationNodeDao.getInformationNodeById(informationNodeId));
    }

    /**
     * Retrieves all information nodes related to the currently authenticated user.
     *
     * @return a List of {@link InformationNodeDto} objects.
     */
    public List<InformationNodeDto> getUserInformationNodes() {
        var user = userDetailsService.getAuthorizedUser();
        return informationNodeDao.getInformationNodesByUserId(user.getId()).parallelStream()
                .map(informationNodeMapper::toDto)
                .toList();
    }

    /**
     * Creates a new information node based on the provided DTO and returns the URI of the newly created node.
     *
     * @param informationNodeDto the DTO containing the information node data.
     * @return the URI of the newly created information node.
     * @throws MilvusException if the operation fails, encapsulating error details and status.
     */
    public URI createInformationNode(final InformationNodeDto informationNodeDto) {
        var user = userDetailsService.getAuthorizedUser();
        var informationNode = informationNodeDao.createInformationNode(
                informationNodeMapper.createEntity(informationNodeDto, user.getId()));
        return URI.create(informationNode.getId().toString());
    }

    /**
     * Updates an existing information node identified by its ID with new data provided in the DTO.
     *
     * @param informationNodeId  the ID of the information node to update.
     * @param informationNodeDto the DTO containing updated data for the information node.
     * @throws MilvusException if the operation fails, encapsulating error details and status.
     */
    public void updateInformationNode(final Long informationNodeId, final InformationNodeDto informationNodeDto) {
        var informationNode = informationNodeDao.getInformationNodeById(informationNodeId);
        informationNodeDao.updateInformationNode(
                informationNodeMapper.updateEntity(informationNode, informationNodeDto));
    }

    /**
     * Deletes an information node identified by its ID.
     *
     * @param informationNodeId the ID of the information node to delete.
     * @throws MilvusException if the operation fails, encapsulating error details and status.
     */
    public void deleteInformationNode(final Long informationNodeId) {
        informationNodeDao.deleteInformationNode(informationNodeDao.getInformationNodeById(informationNodeId));
    }
}
