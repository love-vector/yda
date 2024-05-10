package org.vector.assistant.model.response;

import jakarta.validation.constraints.NotBlank;

public record CommunicationResponse(@NotBlank String message) {}
