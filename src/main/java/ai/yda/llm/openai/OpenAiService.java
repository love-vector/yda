package ai.yda.llm.openai;

import java.util.List;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.assistants.Assistant;
import com.theokanning.openai.assistants.AssistantRequest;
import com.theokanning.openai.assistants.ModifyAssistantRequest;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.threads.Thread;
import com.theokanning.openai.threads.ThreadRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import retrofit2.HttpException;

import org.springframework.ai.openai.api.common.OpenAiApiException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ai.yda.llm.assistant.AssistantDto;

@Service("YDA_OPEN_AI_SERVICE")
@RequiredArgsConstructor
public class OpenAiService {

    @Qualifier(OpenAiConfig.OPENAI_SERVICE_BEAN_NAME)
    private final com.theokanning.openai.service.OpenAiService openAiService;

    @Qualifier(OpenAiConfig.OPENAI_WEB_CLIENT_BEAN_NAME)
    private final WebClient openAiWebClient;

    /**
     * Creates a new assistant in the OpenAI service using the provided {@link AssistantDto}.
     *
     * @param assistantDto the DTO containing the necessary data (name, instructions, model) to create an assistant.
     * @return the newly created {@link Assistant} object containing details of the assistant as created in the OpenAI service.
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure.
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response.
     */
    public Assistant createAssistant(final AssistantDto assistantDto) {
        return openAiService.createAssistant(AssistantRequest.builder()
                .name(assistantDto.name())
                .instructions(assistantDto.instructions())
                .model("gpt-4-1106-preview")
                .build());
    }

    /**
     * Updates the details of an existing assistant in the OpenAI service.
     *
     * @param assistantId the unique identifier of the assistant to be updated in the OpenAI service
     * @param request     the {@link ModifyAssistantRequest} containing the details to update the assistant
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
     */
    public void updateAssistant(final String assistantId, final ModifyAssistantRequest request) {
        openAiService.modifyAssistant(assistantId, request);
    }

    /**
     * Deletes an assistant from the OpenAI service using the provided assistant ID.
     *
     * @param assistantId the unique identifier of the assistant to be deleted from the OpenAI service
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
     */
    public void deleteAssistant(final String assistantId) {
        var result = openAiService.deleteAssistant(assistantId);
        if (!result.isDeleted()) {
            throw new OpenAiApiException("Failed to delete assistant - " + assistantId);
        }
    }

    /**
     * Creates a new thread in the OpenAI service.
     *
     * @return the newly created {@link Thread} object that represents the thread in the OpenAI service.
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
     */
    public Thread createThread() {
        return openAiService.createThread(
                ThreadRequest.builder().messages(List.of()).build());
    }

    /**
     * Adds a message to an existing thread in the OpenAI service.
     *
     * @param threadId the ID of the thread to which the message is to be added
     * @param message  the content of the message to be added to the thread
     * @return the newly created {@link Message} object that represents the added message in the thread
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
     */
    public Message addMessageToThread(final String threadId, final String message) {
        return openAiService.createMessage(
                threadId,
                MessageRequest.builder()
                        .role(ChatMessageRole.USER.value())
                        .content(message)
                        .build());
    }

    /**
     * Initiates a run in the specified thread of the OpenAI service and streams the results.
     *
     * @param assistantId the ID of the assistant used for the run
     * @param threadId    the ID of the thread where the run is to be executed
     * @return a {@link Flux<String>} that streams the results of the run as they become available
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
     */
    public Flux<String> streamRun(final String assistantId, final String threadId) {
        return openAiWebClient
                .post()
                .uri("/v1/threads/{thread_id}/runs", threadId)
                .bodyValue(OpenAiRunDto.builder().assistantId(assistantId).build())
                .retrieve()
                .bodyToFlux(String.class);
    }
}
