package org.vector.assistant.model.request;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(@NotBlank String email, @NotBlank String password) {}
