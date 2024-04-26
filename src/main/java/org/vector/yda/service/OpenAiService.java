package org.vector.yda.service;

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

import org.vector.yda.config.OpenAiConfig;
import org.vector.yda.model.dto.AssistantDto;
import org.vector.yda.model.dto.OpenAiRunDto;

@Service("YDA_OPEN_AI_SERVICE")
@RequiredArgsConstructor
public class OpenAiService {

    @Qualifier(OpenAiConfig.OPENAI_SERVICE_BEAN_NAME)
    private final com.theokanning.openai.service.OpenAiService openAiService;

    @Qualifier(OpenAiConfig.OPENAI_WEB_CLIENT_BEAN_NAME)
    private final WebClient openAiWebClient;

    /**
     * Creates a new assistant in the OpenAI service using the provided {@link AssistantDto}.
     * <p>
     * This method builds an {@link AssistantRequest} from the given DTO, which includes the assistant's name,
     * instructions, and the model specification. It then calls the OpenAI service to create the assistant.
     * This involves a synchronous API call that might throw exceptions related to HTTP errors or issues in parsing
     * the error response from OpenAI.
     *
     * @param assistantDto the DTO containing the necessary data (name, instructions, model) to create an assistant
     * @return the newly created {@link Assistant} object containing details of the assistant as created in the OpenAI service
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *         or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
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
     * <p>
     * This method sends a {@link ModifyAssistantRequest} to the OpenAI service to update the assistant
     * identified by the provided {@code assistantId}. The modifications can include changes to the assistant's
     * settings, behavior, or other attributes as defined in the request.
     *
     * @param assistantId the unique identifier of the assistant to be updated in the OpenAI service
     * @param request the {@link ModifyAssistantRequest} containing the details to update the assistant
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *         or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
     */
    public void updateAssistant(final String assistantId, final ModifyAssistantRequest request) {
        openAiService.modifyAssistant(assistantId, request);
    }

    /**
     * Deletes an assistant from the OpenAI service using the provided assistant ID.
     * <p>
     * This method calls the OpenAI service to delete the assistant identified by the {@code assistantId}.
     * It checks the deletion result, and if the assistant is not successfully deleted, it throws an exception
     * to notify the caller of the failure. This method ensures that the deletion process is verified
     * and handles errors by informing the client through exceptions.
     *
     * @param assistantId the unique identifier of the assistant to be deleted from the OpenAI service
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *         or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
     */
    public void deleteAssistant(final String assistantId) {
        var result = openAiService.deleteAssistant(assistantId);
        if (!result.isDeleted()) {
            throw new OpenAiApiException("Failed to delete assistant - " + assistantId);
        }
    }

    /**
     * Creates a new thread in the OpenAI service.
     * <p>
     * This method initializes a new thread in the OpenAI environment by sending a thread creation request.
     * The request is built with an empty list of messages, implying the creation of an initially empty thread.
     * This could be useful for starting interactions where messages are added in subsequent operations.
     *
     * @return the newly created {@link Thread} object that represents the thread in the OpenAI service.
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *         or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
     */
    public Thread createThread() {
        return openAiService.createThread(
                ThreadRequest.builder().messages(List.of()).build());
    }

    /**
     * Adds a message to an existing thread in the OpenAI service.
     * <p>
     * This method sends a request to add a message with specified content and a defined role to an existing thread
     * identified by {@code threadId}. It constructs a {@link MessageRequest} with the message details and sends it to
     * the OpenAI service. The role for the message is set as "USER", indicating that the message originates from the user.
     *
     * @param threadId the ID of the thread to which the message is to be added
     * @param message the content of the message to be added to the thread
     * @return the newly created {@link Message} object that represents the added message in the thread
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *         or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
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
     * <p>
     * This method posts a request to the OpenAI service to start a new run in a thread identified by {@code threadId}.
     * The run is initiated with the specified {@code assistantId}. It uses reactive programming to stream the results
     * continuously as they are available. The results are returned as a {@link Flux} of {@code String}, where each
     * string represents a part of the response from the run.
     *
     * @param assistantId the ID of the assistant used for the run
     * @param threadId the ID of the thread where the run is to be executed
     * @return a {@link Flux<String>} that streams the results of the run as they become available
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *         or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
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
