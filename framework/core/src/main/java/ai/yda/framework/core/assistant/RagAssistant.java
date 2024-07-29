package ai.yda.framework.core.assistant;

import java.util.List;

import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.core.application.DefaultRagApplication;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

public class RagAssistant extends AbstractAssistant<RagRequest, RagResponse> {

    public RagAssistant(
            final DefaultRagApplication ragApplication, final List<Channel<RagRequest, RagResponse>> channels) {
        super(ragApplication, channels);
    }
}
