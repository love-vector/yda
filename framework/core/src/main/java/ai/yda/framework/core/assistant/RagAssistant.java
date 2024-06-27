package ai.yda.framework.core.assistant;

import java.util.List;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.core.application.RagApplication;

public class RagAssistant<RESPONSE> extends AbstractAssistant<RESPONSE> {

    public RagAssistant(
            RagApplication<BaseAssistantRequest, ?, RESPONSE> ragApplication,
            List<Channel<BaseAssistantRequest, RESPONSE>> channels) {
        super(ragApplication, channels);
    }

    @Override
    public RESPONSE processRequest(BaseAssistantRequest request) {
        return ragApplication.doRag(request);
    }
}
