package ai.yda.framework.core.assistant;

import java.util.List;

import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.core.application.RagApplication;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

public abstract class AbstractAssistant<REQUEST extends RagRequest, RESPONSE extends RagResponse>
        implements Assistant<REQUEST, RESPONSE> {

    private final RagApplication<REQUEST, RESPONSE> ragApplication;

    @Override
    public RESPONSE processRequest(final REQUEST request) {
        return ragApplication.doRag(request);
    }

    public AbstractAssistant(
            final RagApplication<REQUEST, RESPONSE> ragApplication, final List<Channel<REQUEST, RESPONSE>> channels) {
        this.ragApplication = ragApplication;
        channels.forEach(channel -> channel.setAssistant(this));
    }
}
