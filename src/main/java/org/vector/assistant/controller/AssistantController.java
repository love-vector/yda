package org.vector.assistant.controller;

import java.net.URI;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.vector.assistant.dto.assistant.AssistantDTO;
import org.vector.assistant.dto.assistant.CreateAssistantRequest;
import org.vector.assistant.dto.assistant.UpdateAssistantRequest;
import org.vector.assistant.service.AssistantService;

@RestController
@RequestMapping("/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;

    @PostMapping
    public Mono<ResponseEntity<URI>> createAssistant(@RequestBody @Validated final CreateAssistantRequest request) {
        return assistantService.createAssistant(request).map(ResponseEntity::ok);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Flux<AssistantDTO> getAssistants() {
        return assistantService.getAssistants();
    }

    @GetMapping("/{assistantId}")
    public Mono<ResponseEntity<AssistantDTO>> getAssistant(@PathVariable final String assistantId) {
        return assistantService.getAssistant(assistantId).map(ResponseEntity::ok);
    }

    @PutMapping("/{assistantId}")
    public Mono<ResponseEntity<URI>> updateAssistant(
            @PathVariable final String assistantId, @RequestBody @Validated final UpdateAssistantRequest request) {
        return assistantService.updateAssistant(assistantId, request).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{assistantId}")
    public Mono<ResponseEntity<Void>> deleteAssistant(@PathVariable final String assistantId) {
        return assistantService
                .deleteAssistant(assistantId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
