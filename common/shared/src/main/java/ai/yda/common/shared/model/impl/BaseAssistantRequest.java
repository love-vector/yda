package ai.yda.common.shared.model.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ai.yda.common.shared.model.AssistantRequest;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BaseAssistantRequest implements AssistantRequest {

    private String query;

    private String context;
}
