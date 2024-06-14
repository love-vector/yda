package ai.yda.framework.core.channel.factory;

import lombok.Getter;
import lombok.Setter;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.common.shared.model.AssistantResponse;

@Setter
@Getter
public class ChannelConfiguration<REQUEST extends AssistantRequest, RESPONSE extends AssistantResponse> {

    private Class<? extends REQUEST> requestClass;
    private Class<? extends RESPONSE> responseClass;
    private String method;
    // Getters and setters
}
