package org.vector.yda.model.response;

import jakarta.validation.constraints.NotBlank;

public record CommunicationResponse(@NotBlank String message) {}
