package org.vector.assistant.dto;

import lombok.Builder;

@Builder
public record RetrieveResult(Long vectorId, Float score, String collectionName) {}
