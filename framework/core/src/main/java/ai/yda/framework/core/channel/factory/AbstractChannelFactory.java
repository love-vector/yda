package ai.yda.framework.core.channel.factory;

import java.util.Map;

import ai.yda.common.shared.factory.FactoryConfig;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

public abstract class AbstractChannelFactory<REQUEST extends RagRequest, RESPONSE extends RagResponse>
        implements ChannelFactory<REQUEST, RESPONSE> {

    @Override
    public abstract Channel<REQUEST, RESPONSE> createChannel(ChannelConfiguration<REQUEST, RESPONSE> configuration);

    public ChannelConfiguration<REQUEST, RESPONSE> buildConfiguration(
            final Map<? extends FactoryConfig, String> configs,
            final Class<? extends REQUEST> requestClass,
            final Class<? extends RESPONSE> responseClass) {
        ChannelConfiguration<REQUEST, RESPONSE> configuration = new ChannelConfiguration<>();

        configuration.setConfigs(configs);
        configuration.setRequestClass(requestClass);
        configuration.setResponseClass(responseClass);
        return configuration;
    }
}
