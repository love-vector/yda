package ai.yda.llm.assistant;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.yda.llm.openai.OpenAiService;

@Service
@Transactional
@RequiredArgsConstructor
public class AssistantService {

    private final OpenAiService openAiService;

    private final AssistantDao assistantDao;

    private final AssistantMapper assistantMapper;

    /**
     * Retrieves an {@link AssistantDto} for a specific assistant based on the given assistant ID.
     *
     * @param assistantId the unique ID of the assistant to retrieve.
     * @return an {@link AssistantDto} representing the assistant.
     * @throws AssistantNotFoundException if no assistant is found with the provided ID.
     */
    public AssistantDto getAssistant(final Long assistantId) {
        return assistantMapper.toDto(assistantDao.getAssistantById(assistantId));
    }

    /**
     * Retrieves all assistants.
     *
     * @return a List of {@link AssistantDto} objects.
     */
    public List<AssistantDto> getAssistants() {
        return assistantDao.getAssistants().parallelStream()
                .map(assistantMapper::toDto)
                .toList();
    }

    /**
     * Creates a new assistant entry in the database based on the provided {@link AssistantDto} and returns the URI of the created assistant.
     * <p>
     * The creation process involves the following critical steps,
     * which must be performed in the specified order to ensure data consistency and prevent any orphaned records:
     * <ol>
     *   <li>Create the assistant in the OpenAI service using the data provided in the {@code assistantDto}.</li>
     *   <li>Save the new assistant entity to the local database.</li>
     * </ol>
     *
     * @param assistantDto the {@link AssistantDto} containing the necessary data to create a new assistant.
     * @return the {@link URI} of the newly created assistant, constructed from the assistant's ID.
     */
    public URI createAssistant(final AssistantDto assistantDto) {
        var openAiAssistant = openAiService.createAssistant(assistantDto);
        var assistant =
                assistantDao.createAssistant(assistantMapper.createEntity(assistantDto, openAiAssistant.getId()));
        return URI.create(assistant.getId().toString());
    }

    /**
     * Updates an existing assistant in the database and synchronizes changes with the OpenAI service based on the provided {@link AssistantDto}.
     * <p>
     * The update process involves the following critical steps,
     * which must be performed in the specified order to ensure data consistency and prevent any orphaned records:
     * <ol>
     *   <li>Persist the updated assistant data back to the database.</li>
     *   <li>Convert the updated assistant data into a format suitable for the OpenAI service, and send an update request
     *       to the OpenAI service to synchronize the changes.</li>
     * </ol>
     *
     * @param assistantId  the ID of the assistant to be updated.
     * @param assistantDto the {@link AssistantDto} containing updated data for the assistant.
     * @throws AssistantNotFoundException if no assistant is found with the provided ID.
     */
    public void updateAssistant(final Long assistantId, final AssistantDto assistantDto) {
        var assistant = assistantDao.getAssistantById(assistantId);
        assistant = assistantDao.updateAssistant(assistantMapper.updateEntity(assistant, assistantDto));
        openAiService.updateAssistant(assistant.getAssistantId(), assistantMapper.toModifyAssistantRequest(assistant));
    }

    /**
     * Deletes an assistant identified by the given {@code assistantId} from the database and synchronizes the deletion with the OpenAI service.
     * <p>
     * The deletion process involves the following critical steps,
     * which must be performed in the specified order to ensure data consistency and prevent any orphaned records:
     * <ol>
     *   <li>Delete the assistant entity from the local database.</li>
     *   <li>Once the local deletion is confirmed, send a request to the OpenAI service to delete the assistant using
     *       the assistant's OpenAI specific ID.</li>
     * </ol>
     *
     * @param assistantId the ID of the assistant to be deleted.
     * @throws AssistantNotFoundException if no assistant is found with the provided ID.
     */
    public void deleteAssistant(final Long assistantId) {
        var assistant = assistantDao.getAssistantById(assistantId);
        assistantDao.deleteAssistant(assistant);
        openAiService.deleteAssistant(assistant.getAssistantId());
    }
}
