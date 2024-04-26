package org.vector.yda.web.controller;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.vector.yda.model.dto.InformationNodeDto;
import org.vector.yda.service.InformationNodeService;

@RestController
@RequestMapping("/information-nodes")
@RequiredArgsConstructor
public class InformationNodeController {

    private final InformationNodeService informationNodeService;

    @GetMapping
    public ResponseEntity<List<InformationNodeDto>> getUserInformationNodes() {
        return ResponseEntity.ok(informationNodeService.getUserInformationNodes());
    }

    @PostMapping
    public ResponseEntity<URI> createInformationNode(
            @RequestBody @Validated final InformationNodeDto informationNodeDto) {
        return ResponseEntity.created(informationNodeService.createInformationNode(informationNodeDto))
                .build();
    }

    @GetMapping("/{informationNodeId}")
    public ResponseEntity<InformationNodeDto> getInformationNode(@PathVariable final Long informationNodeId) {
        return ResponseEntity.ok(informationNodeService.getInformationNode(informationNodeId));
    }

    @PutMapping("/{informationNodeId}")
    public ResponseEntity<Void> updateInformationNode(
            @PathVariable final Long informationNodeId,
            @RequestBody @Validated final InformationNodeDto informationNodeDto) {
        informationNodeService.updateInformationNode(informationNodeId, informationNodeDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{informationNodeId}")
    public ResponseEntity<Void> deleteInformationNode(@PathVariable final Long informationNodeId) {
        informationNodeService.deleteInformationNode(informationNodeId);
        return ResponseEntity.noContent().build();
    }
}
