package ai.yda.knowledge.internal;

import java.net.URI;
import java.util.List;

import io.milvus.exception.MilvusException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeDao knowledgeDao;

    private final KnowledgeMapper knowledgeMapper;

    /**
     * Retrieves a specific {@link KnowledgeDto} by its ID.
     *
     * @param knowledgeId the ID of the knowledge to retrieve.
     * @return the {@link KnowledgeDto} corresponding to the provided ID.
     * @throws KnowledgeNotFoundException if no knowledge is found with the provided ID.
     */
    public KnowledgeDto getKnowledge(final Long knowledgeId) {
        return knowledgeMapper.toDto(knowledgeDao.getKnowledgeById(knowledgeId));
    }

    /**
     * Retrieves all knowledge.
     *
     * @return a List of {@link KnowledgeDto} objects.
     */
    public List<KnowledgeDto> getKnowledge() {
        return knowledgeDao.getKnowledge().parallelStream()
                .map(knowledgeMapper::toDto)
                .toList();
    }

    /**
     * Creates a knowledge based on the provided DTO and returns the URI of the newly created knowledge.
     *
     * @param knowledgeDto the DTO containing the knowledge data.
     * @return the URI of the newly created knowledge.
     * @throws MilvusException if the operation fails, encapsulating error details and status.
     */
    public URI createKnowledge(final KnowledgeDto knowledgeDto) {
        var knowledge = knowledgeDao.createKnowledge(knowledgeMapper.createEntity(knowledgeDto));
        return URI.create(knowledge.getId().toString());
    }

    /**
     * Updates an existing knowledge identified by its ID with new data provided in the DTO.
     *
     * @param knowledgeId  the ID of the knowledge to update.
     * @param knowledgeDto the DTO containing updated data for the knowledge.
     * @throws MilvusException if the operation fails, encapsulating error details and status.
     */
    public void updateKnowledge(final Long knowledgeId, final KnowledgeDto knowledgeDto) {
        var knowledge = knowledgeDao.getKnowledgeById(knowledgeId);
        knowledgeDao.updateKnowledge(knowledgeMapper.updateEntity(knowledge, knowledgeDto));
    }

    /**
     * Deletes a knowledge identified by its ID.
     *
     * @param knowledgeId the ID of the knowledge to delete.
     * @throws MilvusException if the operation fails, encapsulating error details and status.
     */
    public void deleteKnowledge(final Long knowledgeId) {
        knowledgeDao.deleteKnowledge(knowledgeDao.getKnowledgeById(knowledgeId));
    }
}
