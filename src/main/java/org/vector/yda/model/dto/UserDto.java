package org.vector.yda.model.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDto(@NotBlank String email) {}
