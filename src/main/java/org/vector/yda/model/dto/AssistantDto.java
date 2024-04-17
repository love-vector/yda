package org.vector.yda.model.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AssistantDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) String id,
        @NotBlank String name,
        @NotBlank String instructions,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) OffsetDateTime createdAt) {}
