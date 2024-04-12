package org.vector.assistant.dto.assistant;

import java.time.OffsetDateTime;

import lombok.Builder;

@Builder
public record AssistantDTO(String id, String name, String instructions, OffsetDateTime createdAt) {}
