package org.vector.assistant.dto.assistant;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAssistantRequest(@NotBlank String name, @NotBlank String instruction, @NotNull UUID userId) {}
