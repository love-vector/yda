package org.vector.assistant.model.request;

import jakarta.validation.constraints.NotBlank;

public record DetermineIntentionRequest(@NotBlank String message) {}
