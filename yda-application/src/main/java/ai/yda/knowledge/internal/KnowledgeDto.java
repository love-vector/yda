package ai.yda.knowledge.internal;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KnowledgeDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) Long id, @NotBlank String name, String description) {}
