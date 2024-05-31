package ai.yda.framework.knowledge;

import ai.yda.framework.shared.store.relational.RelationalStore;
import ai.yda.framework.shared.store.vector.VectorStore;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class KnowledgeService<ID> {

    private final RelationalStore<Knowledge<ID>, ID> knowledgeRelationalStore;
    private final VectorStore<Knowledge<ID>> knowledgeVectorStore;

    public Knowledge<ID> getKnowledgeById(final ID knowledgeId) {
        return knowledgeRelationalStore.getById(knowledgeId);
    }

    public Set<Knowledge<ID>> getKnowledge() {
        return knowledgeRelationalStore.getAll();
    }

    public Knowledge<ID> createKnowledge(final Knowledge<ID> knowledge) {
        knowledgeVectorStore.createCollection(knowledge.getName());
        return knowledgeRelationalStore.save(knowledge);
    }

    public void deleteKnowledge(final Knowledge<ID> knowledge) {
        knowledgeVectorStore.deleteCollection(knowledge.getName());
        knowledgeRelationalStore.delete(knowledge);
    }
}
