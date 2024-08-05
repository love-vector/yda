package ai.yda.framework.core.channel.factory;

import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

public interface ChannelFactory<REQUEST extends RagRequest, RESPONSE extends RagResponse> {

    Channel<REQUEST, RESPONSE> createChannel(ChannelConfiguration<REQUEST, RESPONSE> configuration);
}
