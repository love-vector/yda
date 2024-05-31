package ai.yda.framework.intent;

import java.util.*;

import ai.yda.framework.shared.store.vector.Document;
import lombok.RequiredArgsConstructor;

import ai.yda.framework.shared.store.relational.RelationalStore;
import ai.yda.framework.shared.store.vector.VectorStore;

@RequiredArgsConstructor
public class IntentService<ID> {

    private final RelationalStore<Intent<ID>, ID> intentRelationalStore;
    private final VectorStore<Intent<ID>> intentVectorStore;

    public Intent<ID> getIntentById(ID intentId) {
        return intentRelationalStore.getById(intentId);
    }

    public Set<Intent<ID>> getIntents() {
        return intentRelationalStore.getAll();
    }

    public Intent<ID> craeteIntent(final Intent<ID> intent) {
        var document = intentVectorStore.createIntent(intent);
        intent.setVectorId(document.getId());
        return intentRelationalStore.save(intent);
    }

    public void deleteIntent(final Intent<ID> intent) {
        intentRelationalStore.delete(intent);
        intentVectorStore.deleteIntent(intent);
    }

    public List<Document> search(final String message) {
        return intentVectorStore.search(message);
    }
}
