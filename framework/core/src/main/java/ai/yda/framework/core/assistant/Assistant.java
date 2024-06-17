package ai.yda.framework.core.assistant;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.common.shared.model.AssistantResponse;

public interface Assistant<REQUEST extends AssistantRequest, RESPONSE extends AssistantResponse> {

    RESPONSE processRequest(REQUEST request);
}
