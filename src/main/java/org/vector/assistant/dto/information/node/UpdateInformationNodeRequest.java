package org.vector.assistant.dto.information.node;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateInformationNodeRequest(
        @NotBlank String name,
        @NotNull UUID userId,
        @NotBlank String updatedName,
        @NotBlank String updatedDescription) {}
