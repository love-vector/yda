package ai.yda.intent.internal;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class IntentService {

    private final IntentDao intentDao;

    private final IntentMapper intentMapper;

    /**
     * Retrieves a specific {@link IntentDto} by its ID.
     *
     * @param intentId the ID of the intent to retrieve.
     * @return the {@link IntentDto} corresponding to the provided ID.
     */
    public IntentDto getIntent(final Long intentId) {
        return intentMapper.toDto(intentDao.getIntentById(intentId));
    }

    /**
     * Retrieves all intents.
     *
     * @return a List of {@link IntentDto} objects representing all intents.
     */
    public List<IntentDto> getIntents() {
        return intentDao.getIntents().parallelStream().map(intentMapper::toDto).toList();
    }

    /**
     * Creates a new intent based on the provided DTO and returns the URI of the newly created intent.
     *
     * @param intentDto the DTO containing the intent data.
     * @return the URI of the newly created intent.
     */
    public URI createIntent(final IntentDto intentDto) {
        var intent = intentDao.createIntent(intentMapper.createEntity(intentDto));
        return URI.create(intent.getId().toString());
    }

    /**
     * Deletes an intent identified by its ID.
     *
     * @param intentId the ID of the intent to delete.
     */
    public void deleteIntent(final Long intentId) {
        intentDao.deleteIntent(intentDao.getIntentById(intentId));
    }

    /**
     * Determines and ranks intents based on their relevance to a specified message from the {@link DetermineIntentRequest}.
     * This method returns a list of matched intents with associated distances indicating their semantic closeness to the query message.
     *
     * @param request the request containing the message used for searching relevant intents.
     * @return a List of {@link DetermineIntentResponse} objects, each including an {@link IntentDto}
     * representing the matched intent and a distance metric indicating how closely the intent matches the request message.
     */
    public List<DetermineIntentResponse> determineIntent(final DetermineIntentRequest request) {
        return intentDao.search(request).parallelStream()
                .map(document -> {
                    var intent = intentDao.getIntentByVectorId(UUID.fromString(document.getId()));
                    var distance = (Float) document.getMetadata().get("distance");
                    return intentMapper.toDetermineResponse(intent, distance);
                })
                .toList();
    }
}
