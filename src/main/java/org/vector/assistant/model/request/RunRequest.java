package org.vector.assistant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class RunRequest {

    @JsonProperty("assistant_id")
    private String assistantId;

    @JsonProperty("stream")
    private final Boolean isStream = Boolean.TRUE;
}
