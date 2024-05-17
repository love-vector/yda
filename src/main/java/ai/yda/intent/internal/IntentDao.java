package ai.yda.intent.internal;

import java.util.*;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.id.IdGenerator;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ai.yda.shared.vector.store.VectorStoreConfig;

@Component
@Transactional
@RequiredArgsConstructor
public class IntentDao {

    private final IntentRepository intentRepository;

    @Qualifier(VectorStoreConfig.Collection.INTENTS)
    private final VectorStore intentStore;

    private final IdGenerator idGenerator;

    public IntentEntity getIntentById(final Long intentId) {
        return intentRepository.findById(intentId).orElseThrow(IntentNotFoundException::new);
    }

    public IntentEntity getIntentByVectorId(final UUID vectorId) {
        return intentRepository.findByVectorId(vectorId).orElseThrow(IntentNotFoundException::new);
    }

    public Set<IntentEntity> getIntents() {
        return new HashSet<>(intentRepository.findAll());
    }

    public IntentEntity createIntent(final IntentEntity intent) {
        var document = new Document(intent.getDefinition(), Map.of(), idGenerator);
        var createdIntent = intentRepository.save(
                intent.toBuilder().vectorId(UUID.fromString(document.getId())).build());
        intentStore.add(List.of(document));
        return createdIntent;
    }

    public void deleteIntent(final IntentEntity intent) {
        intentRepository.delete(intent);
        intentStore.delete(List.of(intent.getVectorId().toString()));
    }

    public List<Document> search(final DetermineIntentRequest request) {
        return intentStore.similaritySearch(request.message());
    }
}
