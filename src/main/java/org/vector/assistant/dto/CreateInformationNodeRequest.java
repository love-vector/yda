package org.vector.assistant.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateInformationNodeRequest(@NotBlank String name, @NotBlank String description, @NotNull UUID userId) {}
