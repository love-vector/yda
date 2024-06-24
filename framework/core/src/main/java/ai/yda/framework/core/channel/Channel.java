package ai.yda.framework.core.channel;

import reactor.core.publisher.Flux;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.common.shared.model.AssistantResponse;
import ai.yda.framework.core.assistant.Assistant;

public interface Channel<REQUEST extends AssistantRequest, RESPONSE extends AssistantResponse> {
    RESPONSE processRequest(REQUEST request);

    default Flux<RESPONSE> processRequestReactive(REQUEST request) {
        throw new RuntimeException("not available");
    }

    void setAssistant(Assistant<REQUEST, RESPONSE> assistant);
}
