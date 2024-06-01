package ai.yda.channels.internal;

import jakarta.validation.constraints.NotBlank;

public record CommunicationRequest(@NotBlank String message) {}
