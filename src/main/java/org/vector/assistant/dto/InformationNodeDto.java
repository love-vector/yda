package org.vector.assistant.dto;

import lombok.Builder;

@Builder
public record InformationNodeDto(String name, String description, String userId) {}
