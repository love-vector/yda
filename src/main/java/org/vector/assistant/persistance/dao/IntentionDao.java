package org.vector.assistant.persistance.dao;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.id.IdGenerator;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.config.VectorStoreConfig;
import org.vector.assistant.exception.not.found.InformationNodeNotFoundException;
import org.vector.assistant.exception.not.found.IntentionNotFoundException;
import org.vector.assistant.model.request.DetermineIntentionRequest;
import org.vector.assistant.persistance.entity.IntentionEntity;
import org.vector.assistant.persistance.repository.IntentionRepository;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class IntentionDao {

    private final IntentionRepository intentionRepository;

    @Qualifier(VectorStoreConfig.Collection.INTENTIONS)
    private final VectorStore intentionStore;

    private final IdGenerator idGenerator;

    public IntentionEntity getIntentionById(final Long intentionId) {
        return intentionRepository.findById(intentionId).orElseThrow(IntentionNotFoundException::new);
    }

    public IntentionEntity getIntentionByVectorId(final UUID vectorId) {
        return intentionRepository.findByVectorId(vectorId).orElseThrow(InformationNodeNotFoundException::new);
    }

    public List<IntentionEntity> getIntentions() {
        return intentionRepository.findAll();
    }

    public IntentionEntity createIntention(final IntentionEntity intention) {
        var document = new Document(intention.getDefinition(), Map.of(), idGenerator);
        var createdIntention = intentionRepository.save(intention.toBuilder()
                .vectorId(UUID.fromString(document.getId()))
                .build());
        intentionStore.add(List.of(document));
        return createdIntention;
    }

    public void deleteIntention(final IntentionEntity intention) {
        intentionRepository.delete(intention);
        intentionStore.delete(List.of(intention.getVectorId().toString()));
    }

    public List<Document> search(final DetermineIntentionRequest request) {
        return intentionStore.similaritySearch(request.message());
    }
}
