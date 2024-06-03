package ai.yda.framework.rag.channels;

import ai.yda.framework.rag.shared.model.CommunicationRequest;
import ai.yda.framework.rag.shared.model.CommunicationResponse;
import lombok.RequiredArgsConstructor;

import ai.yda.framework.rag.core.CoreService;

@RequiredArgsConstructor
public class DefaultCommunicationChannel implements CommunicationChannel {

    private final CoreService coreService;

    @Override
    public CommunicationResponse sendRequest(final CommunicationRequest request) {
        return new CommunicationResponse(request.message());
    }
}
