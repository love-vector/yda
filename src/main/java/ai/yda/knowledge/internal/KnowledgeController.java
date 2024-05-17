package ai.yda.knowledge.internal;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @GetMapping
    public ResponseEntity<List<KnowledgeDto>> getKnowledge() {
        return ResponseEntity.ok(knowledgeService.getKnowledge());
    }

    @PostMapping
    public ResponseEntity<URI> createKnowledge(@RequestBody @Validated final KnowledgeDto knowledgeDto) {
        return ResponseEntity.created(knowledgeService.createKnowledge(knowledgeDto))
                .build();
    }

    @GetMapping("/{knowledgeId}")
    public ResponseEntity<KnowledgeDto> getKnowledge(@PathVariable final Long knowledgeId) {
        return ResponseEntity.ok(knowledgeService.getKnowledge(knowledgeId));
    }

    @PutMapping("/{knowledgeId}")
    public ResponseEntity<Void> updateKnowledge(
            @PathVariable final Long knowledgeId, @RequestBody @Validated final KnowledgeDto knowledgeDto) {
        knowledgeService.updateKnowledge(knowledgeId, knowledgeDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{knowledgeId}")
    public ResponseEntity<Void> deleteKnowledge(@PathVariable final Long knowledgeId) {
        knowledgeService.deleteKnowledge(knowledgeId);
        return ResponseEntity.noContent().build();
    }
}
