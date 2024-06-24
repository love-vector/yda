package ai.yda.framework.core.assistant;

import reactor.core.publisher.Flux;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.common.shared.model.AssistantResponse;

public interface Assistant<REQUEST extends AssistantRequest, RESPONSE extends AssistantResponse> {

    RESPONSE processRequest(REQUEST request);

    default Flux<RESPONSE> processRequestReactive(REQUEST request) {
        throw new RuntimeException("not available");
    }
}
