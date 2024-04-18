package org.vector.assistant.model.dto;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IntentionDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Long id,
        @NotBlank String name,
        @NotBlank String definition,
        String description) {}
