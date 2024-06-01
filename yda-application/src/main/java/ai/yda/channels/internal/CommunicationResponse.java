package ai.yda.channels.internal;

import jakarta.validation.constraints.NotBlank;

public record CommunicationResponse(@NotBlank String message) {}
