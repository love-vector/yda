package org.vector.assistant.controller;

import java.net.URI;

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
    public Mono<ResponseEntity<InformationNodeDto>> getInformationNode(@PathVariable final Long informationNodeId) {
        return informationNodeService
                .getInformationNode(informationNodeId)
                .map(informationNodeDto -> ResponseEntity.ok(informationNodeDto));
    }

    @PostMapping
    public Mono<ResponseEntity<URI>> createInformationNode(@RequestBody @Validated final InformationNodeDto request) {
        return informationNodeService.createInformationNode(request).map(uri -> ResponseEntity.created(uri)
                .build());
    }

    @PutMapping("/{informationNodeId}")
    public Mono<ResponseEntity<URI>> updateInformationNode(
            @RequestBody @Validated final InformationNodeDto request, @PathVariable final Long informationNodeId) {
        return informationNodeService
                .updateInformationNode(request, informationNodeId)
                .map(uri -> ResponseEntity.ok().location(uri).build());
    }

    @DeleteMapping("/{informationNodeId}")
    public Mono<ResponseEntity<Void>> deleteInformationNode(@PathVariable final Long informationNodeId) {
        return informationNodeService
                .deleteInformationNode(informationNodeId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
