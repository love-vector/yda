package ai.yda.intent.internal;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/intents")
@RequiredArgsConstructor
public class IntentController {

    private final IntentService intentService;

    @GetMapping
    public ResponseEntity<List<IntentDto>> getIntents() {
        return ResponseEntity.ok(intentService.getIntents());
    }

    @PostMapping
    public ResponseEntity<URI> createIntent(@RequestBody @Validated final IntentDto intentDto) {
        return ResponseEntity.created(intentService.createIntent(intentDto)).build();
    }

    @GetMapping("/{intentId}")
    public ResponseEntity<IntentDto> getIntent(@PathVariable final Long intentId) {
        return ResponseEntity.ok(intentService.getIntent(intentId));
    }

    @DeleteMapping("/{intentId}")
    public ResponseEntity<Void> deleteIntent(@PathVariable final Long intentId) {
        intentService.deleteIntent(intentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/determine")
    public ResponseEntity<List<DetermineIntentResponse>> determineIntent(
            @RequestBody @Validated final DetermineIntentRequest request) {
        return ResponseEntity.ok(intentService.determineIntent(request));
    }
}
