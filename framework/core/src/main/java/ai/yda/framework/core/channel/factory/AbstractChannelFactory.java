package ai.yda.framework.core.channel.factory;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.common.shared.model.AssistantResponse;
import ai.yda.framework.core.channel.Channel;

public abstract class AbstractChannelFactory<REQUEST extends AssistantRequest, RESPONSE extends AssistantResponse>
        implements ChannelFactory<REQUEST, RESPONSE> {

    @Override
    public abstract Channel<REQUEST, RESPONSE> createChannel(ChannelConfiguration<REQUEST, RESPONSE> configuration);

    public ChannelConfiguration<REQUEST, RESPONSE> buildConfiguration(
            String method, String url, Class<? extends REQUEST> requestClass, Class<? extends RESPONSE> responseClass) {
        ChannelConfiguration<REQUEST, RESPONSE> configuration = new ChannelConfiguration<>();

        configuration.setMethod(method);
        configuration.setRequestClass(requestClass);
        configuration.setResponseClass(responseClass);
        return configuration;
    }
}
