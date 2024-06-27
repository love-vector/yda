package ai.yda.framework.rag.core.generator;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.framework.rag.core.session.SessionProvider;

public interface Generator<REQUEST extends AssistantRequest, RESPONSE> {

    RESPONSE generate(REQUEST request);

    default SessionProvider getSessionProvider() {
        throw new RuntimeException("Session is not available for this Generator");
    }
}
