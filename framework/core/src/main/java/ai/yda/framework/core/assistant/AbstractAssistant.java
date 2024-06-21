package ai.yda.framework.core.assistant;

import java.util.List;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.core.application.RagApplication;

public abstract class AbstractAssistant implements Assistant<BaseAssistantRequest, BaseAssistantResponse> {

    protected RagApplication<BaseAssistantRequest, ?, BaseAssistantResponse> ragApplication;

    public AbstractAssistant(
            RagApplication<BaseAssistantRequest, ?, BaseAssistantResponse> ragApplication,
            List<Channel<BaseAssistantRequest, BaseAssistantResponse>> channels) {
        this.ragApplication = ragApplication;
        channels.forEach(channel -> channel.setAssistant(this));
    }
}
