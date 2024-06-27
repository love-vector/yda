package ai.yda.framework.core.channel.factory;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import ai.yda.common.shared.factory.FactoryConfig;
import ai.yda.common.shared.model.AssistantRequest;

@Setter
@Getter
public class ChannelConfiguration<REQUEST extends AssistantRequest, RESPONSE> {

    private Class<? extends REQUEST> requestClass;
    private Class<? extends RESPONSE> responseClass;

    private Map<? extends FactoryConfig, String> configs;
}
