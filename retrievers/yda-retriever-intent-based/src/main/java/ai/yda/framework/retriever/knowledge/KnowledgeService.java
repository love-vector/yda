package ai.yda.framework.retriever.knowledge;

import java.util.Set;

import lombok.RequiredArgsConstructor;

import ai.yda.framework.shared.store.relational.RelationalStore;
import ai.yda.framework.shared.store.vector.VectorStore;

@RequiredArgsConstructor
public class KnowledgeService<ID> {

    private final RelationalStore<Knowledge<ID>, ID> relationalStore;
    private final VectorStore vectorStore;

    public Knowledge<ID> getKnowledgeById(final ID knowledgeId) {
        return relationalStore.getById(knowledgeId);
    }

    public Set<Knowledge<ID>> getKnowledge() {
        return relationalStore.getAll();
    }

    public Knowledge<ID> createKnowledge(final Knowledge<ID> knowledge) {
        vectorStore.createCollection(knowledge.getName());
        return relationalStore.save(knowledge);
    }

    public void deleteKnowledge(final Knowledge<ID> knowledge) {
        vectorStore.deleteCollection(knowledge.getName());
        relationalStore.delete(knowledge);
    }
}
