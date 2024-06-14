package ai.yda.common.shared.model.impl;

import lombok.Builder;
import lombok.Getter;

import ai.yda.common.shared.model.AssistantResponse;

@Getter
@Builder(toBuilder = true)
public class BaseAssistantResponse implements AssistantResponse {

    private String content;
}
