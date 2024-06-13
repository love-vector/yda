package ai.yda.framework.core.channel;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.core.assistant.Assistant;

public abstract class AbstractChannel implements Channel<BaseAssistantRequest, BaseAssistantResponse> {

    private Assistant<BaseAssistantRequest, BaseAssistantResponse> assistant;

    @Override
    public BaseAssistantResponse processRequest(BaseAssistantRequest request) {
        return assistant.processRequest(request);
    }

    @Override
    public void setAssistant(Assistant<BaseAssistantRequest, BaseAssistantResponse> assistant) {
        this.assistant = assistant;
    }
}
