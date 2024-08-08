package ai.yda.framework.core.assistant;

import lombok.RequiredArgsConstructor;

import ai.yda.framework.rag.core.Rag;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

@RequiredArgsConstructor
public class RagAssistant implements Assistant<RagRequest, RagResponse> {

    private final Rag<RagRequest, RagResponse> rag;

    @Override
    public RagResponse assist(final RagRequest request) {
        return rag.doRag(request);
    }
}
