package ai.yda.knowledge.internal;

import java.util.HashSet;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ai.yda.shared.vector.store.MilvusDao;

@Component
@Transactional
@RequiredArgsConstructor
public class KnowledgeDao {

    private final KnowledgeRepository knowledgeRepository;

    private final MilvusDao milvusDao;

    public KnowledgeEntity getKnowledgeById(final Long knowledgeId) {
        return knowledgeRepository.findById(knowledgeId).orElseThrow(KnowledgeNotFoundException::new);
    }

    public Set<KnowledgeEntity> getKnowledge() {
        return new HashSet<>(knowledgeRepository.findAll());
    }

    public KnowledgeEntity createKnowledge(final KnowledgeEntity knowledge) {
        var createdKnowledge = knowledgeRepository.save(knowledge);
        milvusDao.createCollectionIfNotExist(createdKnowledge.getName());
        return createdKnowledge;
    }

    public KnowledgeEntity updateKnowledge(final KnowledgeEntity knowledge) {
        return knowledgeRepository.save(knowledge);
    }

    public void deleteKnowledge(final KnowledgeEntity knowledge) {
        knowledgeRepository.delete(knowledge);
        milvusDao.deleteCollectionIfExists(knowledge.getName());
    }
}
