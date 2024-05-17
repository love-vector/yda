package ai.yda.intent.internal;

import jakarta.validation.constraints.NotBlank;

public record DetermineIntentRequest(@NotBlank String message) {}
