package ai.yda.framework.llm.assistant;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

public record AssistantDto(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) String id,
        @NonNull String name,
        @NonNull String instructions,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) OffsetDateTime createdAt) {}
