package org.vector.assistant.dto;

import jakarta.validation.constraints.NotBlank;

public record InformationNodeDto(@NotBlank String name, String description) {}
