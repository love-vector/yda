package ai.yda.framework.rag.core.generator;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.common.shared.model.AssistantResponse;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.session.SessionProvider;

public interface Generator<
        REQUEST extends AssistantRequest, CONTEXT extends RagContext, RESPONSE extends AssistantResponse> {

    RESPONSE generate(REQUEST request, CONTEXT context);

    SessionProvider getSessionProvider();
}
