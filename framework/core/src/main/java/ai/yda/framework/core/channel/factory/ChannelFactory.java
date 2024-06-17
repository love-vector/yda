package ai.yda.framework.core.channel.factory;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.common.shared.model.AssistantResponse;
import ai.yda.framework.core.channel.Channel;

public interface ChannelFactory<REQUEST extends AssistantRequest, RESPONSE extends AssistantResponse> {
    Channel<REQUEST, RESPONSE> createChannel(ChannelConfiguration<REQUEST, RESPONSE> configuration);
}
