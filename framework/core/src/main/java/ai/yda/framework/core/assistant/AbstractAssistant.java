package ai.yda.framework.core.assistant;

import java.util.List;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.core.application.RagApplication;

public abstract class AbstractAssistant<RESPONSE> implements Assistant<BaseAssistantRequest, RESPONSE> {

    protected RagApplication<BaseAssistantRequest, ?, RESPONSE> ragApplication;

    public AbstractAssistant(
            RagApplication<BaseAssistantRequest, ?, RESPONSE> ragApplication,
            List<Channel<BaseAssistantRequest, RESPONSE>> channels) {
        this.ragApplication = ragApplication;
        channels.forEach(channel -> channel.setAssistant(this));
    }
}
