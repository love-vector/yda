package ai.yda.framework.core.channel.factory;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import ai.yda.common.shared.factory.FactoryConfig;
import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.common.shared.model.AssistantResponse;

@Setter
@Getter
public class ChannelConfiguration<REQUEST extends AssistantRequest, RESPONSE extends AssistantResponse> {

    private Class<? extends REQUEST> requestClass;
    private Class<? extends RESPONSE> responseClass;

    private Map<? extends FactoryConfig, String> configs;
}
