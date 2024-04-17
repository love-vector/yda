package org.vector.yda.web.controller;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.vector.yda.model.dto.AssistantDto;
import org.vector.yda.service.AssistantService;

@RestController
@RequestMapping("/assistants")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;

    @GetMapping
    public ResponseEntity<List<AssistantDto>> getUserAssistants() {
        return ResponseEntity.ok(assistantService.getUserAssistants());
    }

    @PostMapping
    public ResponseEntity<URI> createAssistant(@RequestBody @Validated final AssistantDto assistantDto) {
        return ResponseEntity.created(assistantService.createAssistant(assistantDto))
                .build();
    }

    @GetMapping("/{assistantId}")
    public ResponseEntity<AssistantDto> getAssistant(@PathVariable final Long assistantId) {
        return ResponseEntity.ok(assistantService.getAssistant(assistantId));
    }

    @PutMapping("/{assistantId}")
    public ResponseEntity<Void> updateAssistant(
            @PathVariable final Long assistantId, @RequestBody @Validated final AssistantDto assistantDto) {
        assistantService.updateAssistant(assistantId, assistantDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{assistantId}")
    public ResponseEntity<Void> deleteAssistant(@PathVariable final Long assistantId) {
        assistantService.deleteAssistant(assistantId);
        return ResponseEntity.noContent().build();
    }
}
