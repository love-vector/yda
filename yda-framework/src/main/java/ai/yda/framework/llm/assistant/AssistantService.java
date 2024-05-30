package ai.yda.framework.llm.assistant;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssistantService {

    private final LlmProviderService llmProviderService;

    /**
     * Retrieves an {@link AssistantDto} for a specific assistant based on the given assistant ID.
     *
     * @param assistantId the unique ID of the assistant to retrieve.
     * @return an {@link AssistantDto} representing the assistant.
     */
    public AssistantDto getAssistant(final String assistantId) {
        return AssistantMapper.INSTANCE.toDto(llmProviderService.getAssistant(assistantId));
    }

    /**
     * Retrieves all assistants.
     *
     * @return a List of {@link AssistantDto} objects.
     */
    public List<AssistantDto> getAssistants() {
        return llmProviderService.getAssistants().parallelStream()
                .map(AssistantMapper.INSTANCE::toDto)
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
        var assistant = llmProviderService.createAssistant(AssistantMapper.INSTANCE.toCreateRequest(assistantDto));
        return URI.create(assistant.getId());
    }

    /**
     * Updates an existing assistant in the OpenAI service based on the provided {@link AssistantDto}.
     *
     * @param assistantId  the ID of the assistant to be updated.
     * @param assistantDto the {@link AssistantDto} containing updated data for the assistant.
     */
    public void updateAssistant(final String assistantId, final AssistantDto assistantDto) {
        llmProviderService.updateAssistant(assistantId, AssistantMapper.INSTANCE.toModifyRequest(assistantDto));
    }

    /**
     * Deletes an assistant identified by the given {@code assistantId} from the OpenAI service.
     *
     * @param assistantId the ID of the assistant to be deleted.
     */
    public void deleteAssistant(final String assistantId) {
        llmProviderService.deleteAssistant(assistantId);
    }
}
