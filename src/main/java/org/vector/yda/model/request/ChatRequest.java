package org.vector.yda.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatRequest(@NotNull Long assistantId, @NotBlank String message) {}
