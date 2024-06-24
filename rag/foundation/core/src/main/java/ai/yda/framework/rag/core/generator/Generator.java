package ai.yda.framework.rag.core.generator;

import reactor.core.publisher.Flux;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.common.shared.model.AssistantResponse;
import ai.yda.framework.rag.core.session.SessionProvider;

public interface Generator<REQUEST extends AssistantRequest, RESPONSE extends AssistantResponse> {

    RESPONSE generate(REQUEST request);

    default Flux<RESPONSE> generateReactive(REQUEST request) {
        throw new RuntimeException("not implemented");
    }

    default SessionProvider getSessionProvider() {
        throw new RuntimeException("Session is not available for this Generator");
    }
}
