package org.vector.assistant.web.controller;

import java.net.URI;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.vector.assistant.model.dto.IntentionDto;
import org.vector.assistant.model.request.DetermineIntentionRequest;
import org.vector.assistant.model.response.DetermineIntentionResponse;
import org.vector.assistant.service.IntentionService;

@RestController
@RequestMapping("/intentions")
@RequiredArgsConstructor
public class IntentionController {

    private final IntentionService intentionService;

    @GetMapping
    public Flux<IntentionDto> getIntentions() {
        return intentionService.getIntentions();
    }

    @PostMapping
    public Mono<ResponseEntity<URI>> createIntention(@RequestBody @Validated final IntentionDto intentionDto) {
        return intentionService.createIntention(intentionDto).map(uri -> ResponseEntity.created(uri)
                .build());
    }

    @GetMapping("/{intentionId}")
    public Mono<ResponseEntity<IntentionDto>> getIntention(@PathVariable final Long intentionId) {
        return intentionService.getIntention(intentionId).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{intentionId}")
    public Mono<ResponseEntity<Void>> deleteIntention(@PathVariable final Long intentionId) {
        return intentionService
                .deleteIntention(intentionId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PostMapping("/determine")
    public Flux<DetermineIntentionResponse> determineIntention(
            @RequestBody @Validated final DetermineIntentionRequest request) {
        return intentionService.determineIntention(request);
    }
}
