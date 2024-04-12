package org.vector.assistant.dto.assistant;

import jakarta.validation.constraints.NotBlank;

public record UpdateAssistantRequest(@NotBlank String instructions) {}
