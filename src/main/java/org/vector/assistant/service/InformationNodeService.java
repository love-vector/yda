package org.vector.assistant.service;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.model.dto.InformationNodeDto;
import org.vector.assistant.persistance.dao.InformationNodeDao;
import org.vector.assistant.security.CustomUserDetailsService;
import org.vector.assistant.util.mapper.InformationNodeMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InformationNodeService {

    private final CustomUserDetailsService customUserDetailsService;

    private final InformationNodeDao informationNodeDao;

    private final InformationNodeMapper informationNodeMapper;

    /**
     * Retrieves a specific {@link InformationNodeDto} by its ID.
     *
     * @param informationNodeId the ID of the information node to retrieve.
     * @return the {@link InformationNodeDto} corresponding to the provided ID.
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
        var user = customUserDetailsService.getAuthorizedUser();
        return informationNodeDao.getInformationNodesByUserId(user.getId()).parallelStream()
                .map(informationNodeMapper::toDto)
                .toList();
    }

    /**
     * Creates a new information node based on the provided DTO and returns the URI of the newly created node.
     *
     * @param informationNodeDto the DTO containing the information node data.
     * @return the URI of the newly created information node.
     */
    public URI createInformationNode(final InformationNodeDto informationNodeDto) {
        var user = customUserDetailsService.getAuthorizedUser();
        var informationNode = informationNodeDao.createInformationNode(
                informationNodeMapper.toEntity(informationNodeDto, user.getId()));
        return URI.create(informationNode.getId().toString());
    }

    /**
     * Updates an existing information node identified by its ID with new data provided in the DTO.
     *
     * @param informationNodeId  the ID of the information node to update.
     * @param informationNodeDto the DTO containing updated data for the information node.
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
     */
    public void deleteInformationNode(final Long informationNodeId) {
        informationNodeDao.deleteInformationNode(informationNodeDao.getInformationNodeById(informationNodeId));
    }
}
