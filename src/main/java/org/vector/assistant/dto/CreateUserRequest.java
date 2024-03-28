package org.vector.assistant.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(@NotBlank String email, @NotBlank String password) {}
