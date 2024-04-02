package org.vector.assistant.controller;

import java.net.URI;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.vector.assistant.dto.CreateInformationNodeRequest;
import org.vector.assistant.service.InformationNodeService;

@RestController
@RequestMapping("/information-node")
@RequiredArgsConstructor
public class InformationNodeController {

    private final InformationNodeService informationNodeService;

    @PostMapping()
    public Mono<ResponseEntity<URI>> createInformationNode(
            @RequestBody @Valid final CreateInformationNodeRequest request) {
        return informationNodeService
                .createInformationNode(request.name(), request.description(), request.userId())
                .map(uri -> ResponseEntity.created(uri).build());
    }

    @DeleteMapping()
    public Mono<ResponseEntity<URI>> delete(@RequestBody @Valid final CreateInformationNodeRequest request) {
        return informationNodeService
                .deleteInformationNode(request.name(), request.description(), request.userId())
                .map(uri -> ResponseEntity.created(uri).build());
    }
}
