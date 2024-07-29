package ai.yda.framework.core.channel;

import ai.yda.framework.core.assistant.Assistant;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

public abstract class AbstractChannel<REQUEST extends RagRequest, RESPONSE extends RagResponse>
        implements Channel<REQUEST, RESPONSE> {

    private Assistant<REQUEST, RESPONSE> assistant;

    @Override
    public RESPONSE processRequest(final REQUEST request) {
        return assistant.processRequest(request);
    }

    @Override
    public void setAssistant(final Assistant<REQUEST, RESPONSE> assistant) {
        this.assistant = assistant;
    }
}
