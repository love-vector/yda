package org.vector.assistant.channel.integration.demo;

import org.springframework.stereotype.Component;
import org.vector.assistant.channel.integration.ResourceCommunicationChannel;
import org.vector.assistant.model.dto.CommunicationChannelQuery;
import org.vector.assistant.model.dto.CommunicationChannelResponse;

@Component
public class DemoCommunicationChannel implements ResourceCommunicationChannel {

    private CommunicationChannelResponse response;

    @Override
    public void acceptQuery(CommunicationChannelQuery query) {
        this.response.setResponseContent("Response for query" + query.getQueryContent());
    }

    @Override
    public CommunicationChannelResponse getResponse() {
        return response;
    }
}
