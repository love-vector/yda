package ai.yda.framework.azure.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class OpenAiRunDto {

    @JsonProperty("assistant_id")
    private String assistantId;

    @JsonProperty("stream")
    @Builder.Default
    private final Boolean isStream = Boolean.TRUE;
}
