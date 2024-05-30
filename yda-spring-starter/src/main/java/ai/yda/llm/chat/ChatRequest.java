package ai.yda.llm.chat;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(@NotBlank String assistantId, String threadId, @NotBlank String message) {}
