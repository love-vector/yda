package org.vector.assistant.service;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
     * @return the {@link IntentionDto} corresponding to the provided ID.
     */
    public IntentionDto getIntention(final Long intentionId) {
        return intentionMapper.toDto(intentionDao.getIntentionById(intentionId));
    }

    /**
     * Retrieves all intentions.
     *
     * @return a List of {@link IntentionDto} objects.
     */
    public List<IntentionDto> getIntentions() {
        return intentionDao.getIntentions().parallelStream()
                .map(intentionMapper::toDto)
                .toList();
    }

    /**
     * Creates a new intention based on the provided DTO and returns the URI of the newly created intention.
     *
     * @param intentionDto the DTO containing the intention data.
     * @return the URI of the newly created intention.
     */
    public URI createIntention(final IntentionDto intentionDto) {
        var intention = intentionDao.createIntention(intentionMapper.toEntity(intentionDto));
        return URI.create(intention.getId().toString());
    }

    /**
     * Deletes an intention identified by its ID.
     *
     * @param intentionId the ID of the intention to delete.
     */
    public void deleteIntention(final Long intentionId) {
        intentionDao.deleteIntention(intentionDao.getIntentionById(intentionId));
    }

    /**
     * Determines and ranks intentions based on their relevance to the specified message from the {@link DetermineIntentionRequest}.
     * The method returns a list of matched intentions with associated distances indicating semantic closeness to the query message.
     *
     * @param request the request containing the message used for searching relevant intentions.
     * @return a List of {@link DetermineIntentionResponse} objects, each including an {@link IntentionDto}
     * representing the matched intention and a distance metric indicating how closely the intention matches the request message.
     */
    public List<DetermineIntentionResponse> determineIntention(final DetermineIntentionRequest request) {
        return intentionDao.search(request).parallelStream()
                .map(document -> {
                    var intention = intentionDao.getIntentionByVectorId(UUID.fromString(document.getId()));
                    var distance = (Float) document.getMetadata().get("distance");
                    return intentionMapper.toDetermineResponse(intention, distance);
                })
                .toList();
    }
}
