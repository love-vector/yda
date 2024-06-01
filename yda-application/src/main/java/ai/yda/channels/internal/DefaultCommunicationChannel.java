package ai.yda.channels.internal;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ai.yda.core.CoreModuleService;

@Component
@RequiredArgsConstructor
public class DefaultCommunicationChannel implements CommunicationChannel {

    private final CoreModuleService coreService;

    @Override
    public CommunicationResponse sendRequest(final CommunicationRequest request) {
        return new CommunicationResponse("Answer content for request:" + request.message());
    }
}
