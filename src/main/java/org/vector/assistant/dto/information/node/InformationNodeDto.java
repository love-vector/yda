package org.vector.assistant.dto.information.node;

import lombok.Builder;

@Builder
public record InformationNodeDto(String name, String description, String userId) {}
