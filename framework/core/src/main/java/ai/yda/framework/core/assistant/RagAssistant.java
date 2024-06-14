package ai.yda.framework.core.assistant;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.core.application.RagApplication;

public class RagAssistant extends AbstractAssistant {

    public RagAssistant(
            RagApplication<BaseAssistantRequest, ?, BaseAssistantResponse> ragApplication,
            Channel<BaseAssistantRequest, BaseAssistantResponse> channel) {
        super(ragApplication, channel);
    }

    @Override
    public BaseAssistantResponse processRequest(BaseAssistantRequest request) {
        return ragApplication.doRag(request);
    }
}
