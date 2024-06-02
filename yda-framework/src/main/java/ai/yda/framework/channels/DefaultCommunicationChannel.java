package ai.yda.framework.channels;

import ai.yda.framework.shared.model.CommunicationRequest;
import ai.yda.framework.shared.model.CommunicationResponse;
import lombok.RequiredArgsConstructor;

import ai.yda.framework.core.CoreService;

@RequiredArgsConstructor
public class DefaultCommunicationChannel implements CommunicationChannel {

    private final CoreService coreService;

    @Override
    public CommunicationResponse sendRequest(final CommunicationRequest request) {
        return new CommunicationResponse(request.message());
    }
}
