package org.vector.assistant.service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import org.springframework.stereotype.Component;

import org.vector.assistant.security.AuthenticationService;

@Component
@RequiredArgsConstructor
public class ChatService {

    private final AssistantService assistantService;

    private final ThreadService threadService;

    private final AiService aiService;

    private final AuthenticationService authenticationService;

    public Flux<String> chat(final String assistantId, final String userMessage) {
        return authenticationService.getUserId().flatMapMany(userId -> threadService
                .getThread()
                .flatMapMany(
                        thread -> assistantService.getAssistant(assistantId).flatMapMany(assistant -> aiService
                                .addMessageToThread(thread.getId(), userMessage)
                                .flatMapMany(message -> aiService.createRun(message, assistantId)))));
    }
}
