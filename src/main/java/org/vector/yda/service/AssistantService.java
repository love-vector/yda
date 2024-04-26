package org.vector.yda.service;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.yda.exception.not.found.AssistantNotFoundException;
import org.vector.yda.model.dto.AssistantDto;
import org.vector.yda.persistance.dao.AssistantDao;
import org.vector.yda.security.UserDetailsService;
import org.vector.yda.util.mapper.AssistantMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AssistantService {

    private final UserDetailsService userDetailsService;
    private final OpenAiService openAiService;

    private final AssistantDao assistantDao;

    private final AssistantMapper assistantMapper;

    /**
     * Retrieves an {@link AssistantDto} for a specific assistant based on the given assistant ID.
     *
     * @param assistantId the unique ID of the assistant to retrieve
     * @return an {@link AssistantDto} representing the assistant, or {@code null} if no assistant is found with the provided ID
     * @throws AssistantNotFoundException if no assistant is found with the provided ID
     * */
    public AssistantDto getAssistant(final Long assistantId) {
        return assistantMapper.toDto(assistantDao.getAssistantById(assistantId));
    }

    /**
     * Retrieves a list of {@link AssistantDto} representing the assistants associated with the currently authorized user.
     *
     * @return a list of {@link AssistantDto} for the authorized user, which may be empty if the user has no assistants
     */
    public List<AssistantDto> getUserAssistants() {
        var user = userDetailsService.getAuthorizedUser();
        return assistantDao.getAssistantsByUserId(user.getId()).parallelStream()
                .map(assistantMapper::toDto)
                .toList();
    }

    /**
     * Creates a new assistant entry in the database based on the provided {@link AssistantDto} and returns the URI of the created assistant.
     * <p>
     * This method handles the creation process by first obtaining the currently authorized user and then
     * creating an assistant in the OpenAI service. It proceeds to map the DTO to an entity and includes the
     * OpenAI assistant's ID and the authorized user's ID. The assistant is then persisted to the database
     * through {@link AssistantDao}. The URI for the newly created assistant is constructed using the assistant's database ID.
     *
     * @param assistantDto the {@link AssistantDto} containing the necessary data to create a new assistant
     * @return the {@link URI} of the newly created assistant, constructed from the assistant's ID
     */
    public URI createAssistant(final AssistantDto assistantDto) {
        var user = userDetailsService.getAuthorizedUser();
        var openAiAssistant = openAiService.createAssistant(assistantDto);
        var assistant = assistantDao.createAssistant(
                assistantMapper.createEntity(assistantDto, openAiAssistant.getId(), user.getId()));
        return URI.create(assistant.getId().toString());
    }

    /**
     * Updates an existing assistant in the database and synchronizes changes with the OpenAI service based on the provided {@link AssistantDto}.
     * <p>
     * This method updates the details of an assistant identified by {@code assistantId}. It retrieves the existing assistant entity from the database,
     * updates its fields based on the provided {@link AssistantDto}, and then saves these changes back to the database through {@link AssistantDao}.
     * After updating the database, it also sends the updated information to the OpenAI service to keep the assistant data synchronized.
     *
     * @param assistantId the ID of the assistant to be updated
     * @param assistantDto the {@link AssistantDto} containing updated data for the assistant
     * @throws AssistantNotFoundException if no assistant is found with the provided ID
     */
    public void updateAssistant(final Long assistantId, final AssistantDto assistantDto) {
        var assistant = assistantDao.getAssistantById(assistantId);
        assistant = assistantDao.updateAssistant(assistantMapper.updateEntity(assistant, assistantDto));
        openAiService.updateAssistant(assistant.getAssistantId(), assistantMapper.toModifyAssistantRequest(assistant));
    }

    /**
     * Deletes an assistant identified by the given {@code assistantId} from the database and synchronizes the deletion with the OpenAI service.
     * <p>
     * This method first retrieves the assistant entity from the database using the provided {@code assistantId}.
     * If the assistant is found, it is deleted from the database and then the corresponding record in the OpenAI service
     * is also removed to ensure data consistency across platforms. This method handles all steps required to fully remove
     * an assistant record, including database and service level deletions.
     *
     * @param assistantId the ID of the assistant to be deleted
     * @throws AssistantNotFoundException if no assistant is found with the provided ID
     */
    public void deleteAssistant(final Long assistantId) {
        var assistant = assistantDao.getAssistantById(assistantId);
        assistantDao.deleteAssistant(assistant);
        openAiService.deleteAssistant(assistant.getAssistantId());
    }
}
