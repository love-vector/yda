package org.vector.assistant.model.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDto(@NotBlank String email) {}
