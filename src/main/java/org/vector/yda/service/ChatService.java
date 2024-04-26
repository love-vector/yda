package org.vector.yda.service;

import com.theokanning.openai.OpenAiHttpException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import retrofit2.HttpException;

import org.springframework.stereotype.Component;

import org.vector.yda.exception.not.found.AssistantNotFoundException;
import org.vector.yda.model.request.ChatRequest;
import org.vector.yda.persistance.dao.AssistantDao;

@Component
@RequiredArgsConstructor
public class ChatService {

    private final ThreadService threadService;
    private final OpenAiService openAiService;

    private final AssistantDao assistantDao;

    /**
     * Facilitates a chat interaction by sending a message to an assistant and streaming the response.
     *
     * @param request the {@link ChatRequest} containing the assistant ID and the message to be sent
     * @return a {@link Flux<String>} that streams the chat responses as they become available
     * @throws AssistantNotFoundException if no assistant is found with the provided ID from the {@code request}.
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *         or if the OpenAI service returns an error response that can be parsed into a known error structure
     * @throws HttpException if a lower-level HTTP error occurs that does not involve a parseable OpenAI service error response
     */
    public Flux<String> chat(final ChatRequest request) {
        return Mono.fromCallable(() -> assistantDao.getAssistantById(request.assistantId()))
                .flatMapMany(
                        assistant -> Mono.fromCallable(() -> threadService.createOrGetAnyThreadByAssistant(assistant))
                                .flatMap(thread -> Mono.fromCallable(() -> openAiService.addMessageToThread(
                                                thread.getThreadId(), request.message()))
                                        .thenReturn(thread))
                                .flatMapMany(thread ->
                                        openAiService.streamRun(assistant.getAssistantId(), thread.getThreadId())))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
