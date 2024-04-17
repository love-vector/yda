package org.vector.assistant.persistance.dao;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.id.IdGenerator;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.config.VectorStoreConfig;
import org.vector.assistant.exception.not.found.InformationNodeDoesNotExistException;
import org.vector.assistant.exception.not.found.IntentionDoesNotExistException;
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

    public Mono<IntentionEntity> getIntentionById(final Long intentionId) {
        return intentionRepository.findById(intentionId).switchIfEmpty(Mono.error(IntentionDoesNotExistException::new));
    }

    public Mono<IntentionEntity> getIntentionByVectorId(final UUID vectorId) {
        return intentionRepository
                .findByVectorId(vectorId)
                .switchIfEmpty(Mono.error(InformationNodeDoesNotExistException::new));
    }

    public Flux<IntentionEntity> getIntentions() {
        return intentionRepository.findAll();
    }

    public Mono<IntentionEntity> createIntention(final IntentionEntity intention) {
        var document = new Document(intention.getDefinition(), Map.of(), idGenerator);
        return intentionRepository
                .save(intention.toBuilder()
                        .vectorId(UUID.fromString(document.getId()))
                        .build())
                .flatMap(createdIntention -> Mono.fromRunnable(() -> intentionStore.add(List.of(document)))
                        .subscribeOn(Schedulers.boundedElastic())
                        .thenReturn(createdIntention));
    }

    public Mono<Void> deleteIntention(final IntentionEntity intention) {
        return intentionRepository
                .delete(intention)
                .then(Mono.fromRunnable(() -> intentionStore.delete(
                                List.of(intention.getVectorId().toString())))
                        .subscribeOn(Schedulers.boundedElastic()))
                .then();
    }

    public Flux<Document> search(final DetermineIntentionRequest request) {
        return Mono.fromCallable(() -> intentionStore.similaritySearch(request.message()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapIterable(documents -> documents);
    }
}
