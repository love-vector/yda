package ai.yda.framework.core.channel;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.framework.core.assistant.Assistant;

public interface Channel<REQUEST extends AssistantRequest, RESPONSE> {
    RESPONSE processRequest(REQUEST request);

    void setAssistant(Assistant<REQUEST, RESPONSE> assistant);
}
