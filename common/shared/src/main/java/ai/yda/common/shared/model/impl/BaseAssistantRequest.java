package ai.yda.common.shared.model.impl;

import lombok.*;

import ai.yda.common.shared.model.AssistantRequest;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BaseAssistantRequest implements AssistantRequest {

    private String content;
}
