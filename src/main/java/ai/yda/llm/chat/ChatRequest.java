package ai.yda.llm.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatRequest(@NotNull Long assistantId, @NotBlank String message) {}
