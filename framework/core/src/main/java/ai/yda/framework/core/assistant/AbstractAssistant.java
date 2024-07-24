package ai.yda.framework.core.assistant;

import java.util.List;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.core.application.RagApplication;

public abstract class AbstractAssistant<RESPONSE> implements Assistant<BaseAssistantRequest, RESPONSE> {

    private final RagApplication<BaseAssistantRequest, ?, RESPONSE> ragApplication;

    @Override
    public RESPONSE processRequest(final BaseAssistantRequest request) {
        return ragApplication.doRag(request);
    }

    public AbstractAssistant(
            final RagApplication<BaseAssistantRequest, ?, RESPONSE> ragApplication,
            final List<Channel<BaseAssistantRequest, RESPONSE>> channels) {
        this.ragApplication = ragApplication;
        channels.forEach(channel -> channel.setAssistant(this));
    }
}
