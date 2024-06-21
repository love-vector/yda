package ai.yda.framework.core.assistant;

import java.util.List;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.core.application.RagApplication;

public class RagAssistant extends AbstractAssistant {

    public RagAssistant(
            RagApplication<BaseAssistantRequest, ?, BaseAssistantResponse> ragApplication,
            List<Channel<BaseAssistantRequest, BaseAssistantResponse>> channels) {
        super(ragApplication, channels);
    }

    @Override
    public BaseAssistantResponse processRequest(BaseAssistantRequest request) {
        return ragApplication.doRag(request);
    }
}
