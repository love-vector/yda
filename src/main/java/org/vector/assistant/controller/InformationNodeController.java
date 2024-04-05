package org.vector.assistant.controller;

import java.net.URI;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.vector.assistant.dto.information.node.CreateInformationNodeRequest;
import org.vector.assistant.dto.information.node.DeleteInformationNodeRequest;
import org.vector.assistant.dto.information.node.GetInformationNodeRequest;
import org.vector.assistant.dto.information.node.UpdateInformationNodeRequest;
import org.vector.assistant.persistance.entity.InformationNodeEntity;
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

    @GetMapping
    public Mono<ResponseEntity<InformationNodeEntity>> getInformationNode(
            @RequestBody @Valid final GetInformationNodeRequest request) {
        return informationNodeService.getInformationNode(request.name(), request.userId());
    }

    @DeleteMapping()
    public Mono<ResponseEntity<URI>> deleteInformationNode(
            @RequestBody @Valid final DeleteInformationNodeRequest request) {
        return informationNodeService
                .deleteInformationNode(request.name(), request.userId())
                .map(uri -> ResponseEntity.created(uri).build());
    }

    @PutMapping()
    public Mono<ResponseEntity<URI>> updateInformationNode(
            @RequestBody @Valid final UpdateInformationNodeRequest request) {
        return informationNodeService.updateInformationNode(
                request.name(), request.userId(), request.updatedName(), request.updatedDescription());
    }
}
