package org.vector.yda.web.controller;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.vector.yda.model.request.ChatRequest;
import org.vector.yda.service.ChatService;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody @Validated final ChatRequest request) {
        return chatService.chat(request);
    }
}
