package ai.yda.framework.core.assistant;

import java.util.List;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.core.application.RagApplication;

public class RagAssistant<RESPONSE> extends AbstractAssistant<RESPONSE> {

    public RagAssistant(
            final RagApplication<BaseAssistantRequest, ?, RESPONSE> ragApplication,
            final List<Channel<BaseAssistantRequest, RESPONSE>> channels) {
        super(ragApplication, channels);
    }
}
