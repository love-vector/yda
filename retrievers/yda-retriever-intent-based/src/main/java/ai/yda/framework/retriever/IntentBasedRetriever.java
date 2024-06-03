package ai.yda.framework.retriever;

import ai.yda.framework.rag.retriever.Retriever;
import ai.yda.framework.rag.shared.model.CommunicationRequest;
import ai.yda.framework.rag.shared.model.RawContext;

public class IntentBasedRetriever implements Retriever<RawContext, CommunicationRequest> {

    @Override
    public RawContext retrieve(CommunicationRequest request) {
        return null;
    }
}
