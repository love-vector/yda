package org.vector.yda.web.controller;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.vector.yda.model.dto.IntentionDto;
import org.vector.yda.model.request.DetermineIntentionRequest;
import org.vector.yda.model.response.DetermineIntentionResponse;
import org.vector.yda.service.IntentionService;

@RestController
@RequestMapping("/intentions")
@RequiredArgsConstructor
public class IntentionController {

    private final IntentionService intentionService;

    @GetMapping
    public ResponseEntity<List<IntentionDto>> getIntentions() {
        return ResponseEntity.ok(intentionService.getIntentions());
    }

    @PostMapping
    public ResponseEntity<URI> createIntention(@RequestBody @Validated final IntentionDto intentionDto) {
        return ResponseEntity.created(intentionService.createIntention(intentionDto))
                .build();
    }

    @GetMapping("/{intentionId}")
    public ResponseEntity<IntentionDto> getIntention(@PathVariable final Long intentionId) {
        return ResponseEntity.ok(intentionService.getIntention(intentionId));
    }

    @DeleteMapping("/{intentionId}")
    public ResponseEntity<Void> deleteIntention(@PathVariable final Long intentionId) {
        intentionService.deleteIntention(intentionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/determine")
    public ResponseEntity<List<DetermineIntentionResponse>> determineIntention(
            @RequestBody @Validated final DetermineIntentionRequest request) {
        return ResponseEntity.ok(intentionService.determineIntention(request));
    }
}
