package ai.yda.framework.core.channel;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.core.assistant.Assistant;

public abstract class AbstractChannel<RESPONSE> implements Channel<BaseAssistantRequest, RESPONSE> {

    private Assistant<BaseAssistantRequest, RESPONSE> assistant;

    @Override
    public RESPONSE processRequest(BaseAssistantRequest request) {
        return assistant.processRequest(request);
    }

    @Override
    public void setAssistant(Assistant<BaseAssistantRequest, RESPONSE> assistant) {
        this.assistant = assistant;
    }
}
