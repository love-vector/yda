package org.vector.assistant.service;

import java.net.URI;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.model.dto.IntentionDto;
import org.vector.assistant.model.request.DetermineIntentionRequest;
import org.vector.assistant.model.response.DetermineIntentionResponse;
import org.vector.assistant.persistance.dao.IntentionDao;
import org.vector.assistant.util.mapper.IntentionMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class IntentionService {

    private final IntentionDao intentionDao;

    private final IntentionMapper intentionMapper;

    /**
     * Retrieves a specific {@link IntentionDto} by its ID.
     *
     * @param intentionId the ID of the intention to retrieve.
     * @return a {@link Mono} emitting the {@link IntentionDto}.
     */
    public Mono<IntentionDto> getIntention(final Long intentionId) {
        return intentionDao.getIntentionById(intentionId).map(intentionMapper::toDto);
    }

    /**
     * Retrieves all intentions.
     *
     * @return a {@link Flux} emitting {@link IntentionDto} objects.
     */
    public Flux<IntentionDto> getIntentions() {
        return intentionDao.getIntentions().map(intentionMapper::toDto);
    }

    /**
     * Creates a new intention based on the provided DTO and returns the URI of the newly created intention.
     *
     * @param intentionDto the DTO containing the intention data.
     * @return a {@link Mono} emitting the URI of the newly created intention.
     */
    public Mono<URI> createIntention(final IntentionDto intentionDto) {
        return intentionDao
                .createIntention(intentionMapper.toEntity(intentionDto))
                .map(entity -> URI.create(entity.getId().toString()));
    }

    /**
     * Deletes an intention identified by its ID.
     *
     * @param intentionId the ID of the intention to delete.
     * @return a {@link Mono} signaling completion or error if the deletion fails.
     */
    public Mono<Void> deleteIntention(final Long intentionId) {
        return intentionDao.getIntentionById(intentionId).flatMap(intentionDao::deleteIntention);
    }

    /**
     * Determines and ranks intentions based on their relevance to the specified message from the {@link DetermineIntentionRequest}.
     * The search is performed based on the semantic similarity between the stored intentions and the provided message.
     * Results are ranked by the distance metric indicating closeness to the query message, with a smaller distance representing a higher relevance.
     *
     * @param request the request containing the message used for searching relevant intentions.
     * @return a {@link Flux} emitting {@link DetermineIntentionResponse} objects, each including an {@link IntentionDto}
     * representing the matched intention and a distance metric indicating how closely the intention matches the request message.
     */
    public Flux<DetermineIntentionResponse> determineIntention(final DetermineIntentionRequest request) {
        return intentionDao.search(request).flatMap(document -> intentionDao
                .getIntentionByVectorId(UUID.fromString(document.getId()))
                .map(intention -> {
                    var distance = (Float) document.getMetadata().get("distance");
                    return intentionMapper.toDetermineResponse(intention, distance);
                }));
    }
}
