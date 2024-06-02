package ai.yda.framework.retriever;

import ai.yda.framework.shared.model.CommunicationRequest;
import ai.yda.framework.shared.model.RawContext;

public class IntentBasedRetriever implements Retriever<RawContext, CommunicationRequest> {

    @Override
    public RawContext retrieve(CommunicationRequest request) {
        return null;
    }
}
