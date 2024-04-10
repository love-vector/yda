package org.vector.assistant.controller;

import java.net.URI;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.vector.assistant.dto.information.node.*;
import org.vector.assistant.service.InformationNodeService;

@RestController
@RequestMapping("/information-node")
@RequiredArgsConstructor
public class InformationNodeController {

    private final InformationNodeService informationNodeService;

    @GetMapping("/{informationNodeId}")
    public Mono<ResponseEntity<InformationNodeDto>> getInformationNode(@PathVariable final UUID informationNodeId) {
        return informationNodeService.getInformationNode(informationNodeId);
    }

    @PostMapping
    public Mono<ResponseEntity<URI>> createInformationNode(@RequestBody @Validated final InformationNodeDto request) {
        return informationNodeService.createInformationNode(request).map(uri -> ResponseEntity.created(uri)
                .build());
    }

    @PutMapping
    public Mono<ResponseEntity<URI>> updateInformationNode(@RequestBody @Validated final InformationNodeDto request) {
        return informationNodeService.updateInformationNode(request);
    }

    @DeleteMapping("/{informationNodeId}")
    public Mono<ResponseEntity<URI>> deleteInformationNode(@PathVariable final UUID informationNodeId) {
        return informationNodeService.deleteInformationNode(informationNodeId);
    }
}
