package org.vector.assistant.service;

import java.util.Collections;

import com.theokanning.openai.DeleteResult;
import com.theokanning.openai.assistants.*;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.Thread;
import com.theokanning.openai.threads.ThreadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.stereotype.Component;

import org.vector.assistant.client.RunClient;
import org.vector.assistant.dto.RunRequest;
import org.vector.assistant.dto.assistant.CreateAssistantRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final OpenAiService openAiService;
    private final RunClient runClient;

    public Mono<Assistant> createAssistant(CreateAssistantRequest request) {
        return Mono.fromCallable(() -> openAiService.createAssistant(AssistantRequest.builder()
                        .name(request.name())
                        .instructions(request.instruction())
                        .model("gpt-4-1106-preview")
                        .build()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Assistant> getAssistant(final String assistantId) {
        return Mono.fromCallable(() -> openAiService.retrieveAssistant(assistantId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Assistant> updateAssistant(String assistantId, ModifyAssistantRequest request) {
        return Mono.fromCallable(() -> openAiService.modifyAssistant(assistantId, request))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<DeleteResult> deleteAssistant(final String assistantId) {
        return Mono.fromCallable(() -> openAiService.deleteAssistant(assistantId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Thread> createThread() {
        return Mono.fromCallable(() -> openAiService.createThread(
                ThreadRequest.builder().messages(Collections.emptyList()).build()));
    }

    public Mono<Message> addMessageToThread(final String threadId, final String userMessage) {
        return Mono.fromCallable(() -> openAiService.createMessage(
                        threadId,
                        MessageRequest.builder()
                                .role(ChatMessageRole.USER.value())
                                .content(userMessage)
                                .build()))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(message -> log.debug("Created Message: {}", message))
                .doOnError(throwable -> log.error("Error create Message: {}", throwable.getMessage()));
    }

    public Flux<String> createRun(final Message message, final String assistantId) {
        return runClient
                .createRunStream(
                        message.getThreadId(),
                        RunRequest.builder().assistantId(assistantId).build())
                .doOnSubscribe(subscription -> log.debug(
                        "Created Run Stream with Assistant: {} and Thread: {}",
                        message.getAssistantId(),
                        message.getThreadId()))
                .doOnError(throwable -> log.error("Error create Run: {}", throwable.getMessage()));
    }
}
