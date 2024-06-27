package ai.yda.framework.core.assistant;

import ai.yda.common.shared.model.AssistantRequest;

public interface Assistant<REQUEST extends AssistantRequest, RESPONSE> {

    RESPONSE processRequest(REQUEST request);
}
