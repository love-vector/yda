package ai.yda.framework.core.assistant;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.core.application.RagApplication;

public abstract class AbstractAssistant implements Assistant<BaseAssistantRequest, BaseAssistantResponse> {

    protected RagApplication<BaseAssistantRequest, ?, BaseAssistantResponse> ragApplication;

    public AbstractAssistant(
            RagApplication<BaseAssistantRequest, ?, BaseAssistantResponse> ragApplication,
            Channel<BaseAssistantRequest, BaseAssistantResponse> channel) {
        this.ragApplication = ragApplication;
        channel.setAssistant(this);
    }
}
