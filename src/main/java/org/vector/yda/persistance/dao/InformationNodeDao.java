package org.vector.yda.persistance.dao;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.vector.yda.exception.not.found.InformationNodeNotFoundException;
import org.vector.yda.persistance.entity.InformationNodeEntity;
import org.vector.yda.persistance.repository.InformationNodeRepository;

@Component
@Transactional
@RequiredArgsConstructor
public class InformationNodeDao {

    private final InformationNodeRepository informationNodeRepository;

    private final MilvusDao milvusDao;

    public InformationNodeEntity getInformationNodeById(final Long informationNodeId) {
        return informationNodeRepository.findById(informationNodeId).orElseThrow(InformationNodeNotFoundException::new);
    }

    public List<InformationNodeEntity> getInformationNodesByUserId(final UUID userId) {
        return informationNodeRepository.findAllByUserId(userId);
    }

    public InformationNodeEntity createInformationNode(final InformationNodeEntity informationNode) {
        var createdInformationNode = informationNodeRepository.save(informationNode);
        milvusDao.createCollectionIfNotExist(createdInformationNode.getCollectionName());
        return createdInformationNode;
    }

    public InformationNodeEntity updateInformationNode(final InformationNodeEntity informationNode) {
        return informationNodeRepository.save(informationNode);
    }

    public void deleteInformationNode(final InformationNodeEntity informationNode) {
        informationNodeRepository.delete(informationNode);
        milvusDao.deleteCollectionIfExists(informationNode.getCollectionName());
    }
}
