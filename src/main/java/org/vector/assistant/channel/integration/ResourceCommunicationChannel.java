package org.vector.assistant.channel.integration;

import org.vector.assistant.model.dto.CommunicationChannelQuery;
import org.vector.assistant.model.dto.CommunicationChannelResponse;

public interface ResourceCommunicationChannel {

    void acceptQuery(CommunicationChannelQuery query);

    CommunicationChannelResponse getResponse();
}
