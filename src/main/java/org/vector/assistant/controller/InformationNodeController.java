package org.vector.assistant.controller;

import java.net.URI;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.vector.assistant.dto.InformationNodeDto;
import org.vector.assistant.service.InformationNodeService;

@RestController
@RequestMapping("/information-node")
@RequiredArgsConstructor
public class InformationNodeController {

    private final InformationNodeService informationNodeService;

    @GetMapping
    public Flux<InformationNodeDto> getUserInformationNodes() {
        return informationNodeService.getUserInformationNodes();
    }

    @PostMapping
    public Mono<ResponseEntity<URI>> createInformationNode(
            @RequestBody @Validated final InformationNodeDto informationNodeDto) {
        return informationNodeService.createInformationNode(informationNodeDto).map(uri -> ResponseEntity.created(uri)
                .build());
    }

    @GetMapping("/{informationNodeId}")
    public Mono<ResponseEntity<InformationNodeDto>> getInformationNode(@PathVariable final Long informationNodeId) {
        return informationNodeService.getInformationNode(informationNodeId).map(ResponseEntity::ok);
    }

    @PutMapping("/{informationNodeId}")
    public Mono<ResponseEntity<Void>> updateInformationNode(
            @PathVariable final Long informationNodeId,
            @RequestBody @Validated final InformationNodeDto informationNodeDto) {
        return informationNodeService
                .updateInformationNode(informationNodeId, informationNodeDto)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{informationNodeId}")
    public Mono<ResponseEntity<Void>> deleteInformationNode(@PathVariable final Long informationNodeId) {
        return informationNodeService
                .deleteInformationNode(informationNodeId)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
