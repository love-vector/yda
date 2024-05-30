package ai.yda.framework.llm.assistant;

import java.util.List;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.assistants.Assistant;
import com.theokanning.openai.assistants.AssistantRequest;
import com.theokanning.openai.assistants.ModifyAssistantRequest;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.threads.Thread;
import com.theokanning.openai.threads.ThreadRequest;
import reactor.core.publisher.Flux;
import retrofit2.HttpException;

import org.springframework.ai.openai.api.common.OpenAiApiException;

public interface LlmProviderService {

    /**
     * Retrieves the details of a specific assistant from the OpenAI service using the provided assistant ID.
     *
     * @param assistantId the unique identifier of the assistant to be retrieved from the OpenAI service.
     * @return the {@link Assistant} object containing the details of the requested assistant.
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure.
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response.
     */
    Assistant getAssistant(final String assistantId);

    /**
     * Retrieves a list of assistants from the OpenAI service.
     *
     * @return a list of {@link Assistant} objects available in the OpenAI service.
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure.
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response.
     */
    List<Assistant> getAssistants();

    /**
     * Creates a new assistant in the OpenAI service using the provided {@link AssistantRequest}.
     *
     * @param request the {@link AssistantRequest} containing the necessary data (name, instructions, model) to create an assistant.
     * @return the newly created {@link Assistant} object containing details of the assistant as created in the OpenAI service.
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure.
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response.
     */
    Assistant createAssistant(final AssistantRequest request);

    /**
     * Updates the details of an existing assistant in the OpenAI service.
     *
     * @param assistantId the unique identifier of the assistant to be updated in the OpenAI service.
     * @param request     the {@link ModifyAssistantRequest} containing the details to update the assistant.
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure.
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response.
     */
    void updateAssistant(final String assistantId, final ModifyAssistantRequest request);

    /**
     * Deletes an assistant from the OpenAI service using the provided assistant ID.
     *
     * @param assistantId the unique identifier of the assistant to be deleted from the OpenAI service.
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure.
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response.
     * @throws OpenAiApiException  if the deletion operation fails as indicated by the OpenAI service response.
     */
    void deleteAssistant(final String assistantId);

    /**
     * Creates a new thread in the OpenAI service.
     *
     * @param request the {@link ThreadRequest} object containing the details for the new thread.
     * @return the newly created {@link Thread} object that represents the thread in the OpenAI service.
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
     */
    Thread createThread(final ThreadRequest request);

    /**
     * Adds a message to an existing thread in the OpenAI service.
     *
     * @param threadId the ID of the thread to which the message is to be added.
     * @param request  the {@link MessageRequest} object containing the content of the message to be added to the thread.
     * @return the newly created {@link Message} object that represents the added message in the thread.
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
     */
    Message createMessage(final String threadId, final MessageRequest request);

    /**
     * Initiates a run in the specified thread of the OpenAI service and streams the results.
     *
     * @param assistantId the ID of the assistant used for the run.
     * @param threadId    the ID of the thread where the run is to be executed.
     * @return a {@link Flux <String>} that streams the results of the run as they become available.
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
     */
    Flux<String> streamRun(final String assistantId, final String threadId);
}
