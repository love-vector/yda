package org.vector.assistant.dto.information.node;

import jakarta.validation.constraints.NotBlank;

import lombok.Builder;

@Builder
public record InformationNodeDto(@NotBlank String name, @NotBlank String description) {}
