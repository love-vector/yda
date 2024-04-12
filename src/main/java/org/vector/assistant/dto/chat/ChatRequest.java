package org.vector.assistant.dto.chat;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(@NotBlank String assistantId, @NotBlank String userMessage) {}
