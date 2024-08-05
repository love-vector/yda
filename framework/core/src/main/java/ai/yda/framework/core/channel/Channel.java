package ai.yda.framework.core.channel;

import ai.yda.framework.core.assistant.Assistant;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

public interface Channel<REQUEST extends RagRequest, RESPONSE extends RagResponse> {

    RESPONSE processRequest(REQUEST request);

    void setAssistant(Assistant<REQUEST, RESPONSE> assistant);
}
