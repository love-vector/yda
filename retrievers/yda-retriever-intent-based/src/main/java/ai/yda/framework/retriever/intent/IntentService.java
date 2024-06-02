package ai.yda.framework.retriever.intent;

import java.util.*;

import ai.yda.framework.retriever.intent.Intent;
import lombok.RequiredArgsConstructor;

import org.springframework.ai.document.Document;

import ai.yda.framework.shared.store.relational.RelationalStore;
import ai.yda.framework.shared.store.vector.VectorStore;

@RequiredArgsConstructor
public class IntentService<ID> {

    private final RelationalStore<Intent<ID>, ID> relationalStore;
    private final VectorStore vectorStore;

    public Intent<ID> getIntentById(ID intentId) {
        return relationalStore.getById(intentId);
    }

    public Set<Intent<ID>> getIntents() {
        return relationalStore.getAll();
    }

    public Intent<ID> craeteIntent(final Intent<ID> intent) {
        var document = new Document(intent.getDefinition());
        vectorStore.add(List.of(document));
        intent.setVectorId(document.getId());
        return relationalStore.save(intent);
    }

    public void deleteIntent(final Intent<ID> intent) {
        relationalStore.delete(intent);
        vectorStore.delete(List.of(intent.getVectorId()));
    }

    public List<Document> search(final String message) {
        return vectorStore.similaritySearch(message);
    }
}
