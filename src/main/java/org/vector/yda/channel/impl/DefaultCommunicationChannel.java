package org.vector.yda.channel.impl;

import org.springframework.stereotype.Component;

import org.vector.yda.channel.integration.CommunicationChannel;
import org.vector.yda.model.request.CommunicationRequest;
import org.vector.yda.model.response.CommunicationResponse;

@Component
public class DefaultCommunicationChannel implements CommunicationChannel {

    @Override
    public CommunicationResponse sendRequest(final CommunicationRequest request) {
        return new CommunicationResponse("Answer content for request:" + request.message());
    }
}
