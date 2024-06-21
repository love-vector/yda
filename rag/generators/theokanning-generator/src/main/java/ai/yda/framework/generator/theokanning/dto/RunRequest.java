package ai.yda.framework.generator.theokanning.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class RunRequest {

    @JsonProperty("additional_instructions")
    private String additionalInstructions;

    @JsonProperty("assistant_id")
    private String assistantId;

    @JsonProperty("stream")
    private final Boolean isStream = Boolean.TRUE;
}
