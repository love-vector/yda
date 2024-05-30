package ai.yda.llm.assistant;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import ai.yda.llm.openai.OpenAiService;

@Service
@RequiredArgsConstructor
public class AssistantService {

    private final OpenAiService openAiService;

    private final AssistantMapper assistantMapper;

    /**
     * Retrieves an {@link AssistantDto} for a specific assistant based on the given assistant ID.
     *
     * @param assistantId the unique ID of the assistant to retrieve.
     * @return an {@link AssistantDto} representing the assistant.
     */
    public AssistantDto getAssistant(final String assistantId) {
        return assistantMapper.toDto(openAiService.getAssistant(assistantId));
    }

    /**
     * Retrieves all assistants.
     *
     * @return a List of {@link AssistantDto} objects.
     */
    public List<AssistantDto> getAssistants() {
        return openAiService.getAssistants().parallelStream()
                .map(assistantMapper::toDto)
                .toList();
    }

    /**
     * Creates a new assistant in the OpenAI service based on the provided {@link AssistantDto}
     * and returns the URI of the created assistant.
     *
     * @param assistantDto the {@link AssistantDto} containing the necessary data to create a new assistant.
     * @return the {@link URI} of the newly created assistant, constructed from the assistant's ID.
     */
    public URI createAssistant(final AssistantDto assistantDto) {
        var assistant = openAiService.createAssistant(assistantMapper.toCreateRequest(assistantDto));
        return URI.create(assistant.getId());
    }

    /**
     * Updates an existing assistant in the OpenAI service based on the provided {@link AssistantDto}.
     *
     * @param assistantId  the ID of the assistant to be updated.
     * @param assistantDto the {@link AssistantDto} containing updated data for the assistant.
     */
    public void updateAssistant(final String assistantId, final AssistantDto assistantDto) {
        openAiService.updateAssistant(assistantId, assistantMapper.toModifyRequest(assistantDto));
    }

    /**
     * Deletes an assistant identified by the given {@code assistantId} from the OpenAI service.
     *
     * @param assistantId the ID of the assistant to be deleted.
     */
    public void deleteAssistant(final String assistantId) {
        openAiService.deleteAssistant(assistantId);
    }
}
