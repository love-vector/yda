package ai.yda.llm.assistant;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assistants")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;

    @GetMapping
    public ResponseEntity<List<AssistantDto>> getAssistants() {
        return ResponseEntity.ok(assistantService.getAssistants());
    }

    @PostMapping
    public ResponseEntity<URI> createAssistant(@RequestBody @Validated final AssistantDto assistantDto) {
        return ResponseEntity.created(assistantService.createAssistant(assistantDto))
                .build();
    }

    @GetMapping("/{assistantId}")
    public ResponseEntity<AssistantDto> getAssistant(@PathVariable final String assistantId) {
        return ResponseEntity.ok(assistantService.getAssistant(assistantId));
    }

    @PutMapping("/{assistantId}")
    public ResponseEntity<Void> updateAssistant(
            @PathVariable final String assistantId, @RequestBody @Validated final AssistantDto assistantDto) {
        assistantService.updateAssistant(assistantId, assistantDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{assistantId}")
    public ResponseEntity<Void> deleteAssistant(@PathVariable final String assistantId) {
        assistantService.deleteAssistant(assistantId);
        return ResponseEntity.noContent().build();
    }
}
