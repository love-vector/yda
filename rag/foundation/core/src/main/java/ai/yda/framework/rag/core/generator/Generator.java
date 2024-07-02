package ai.yda.framework.rag.core.generator;

import ai.yda.common.shared.model.AssistantRequest;

public interface Generator<REQUEST extends AssistantRequest, RESPONSE> {

    RESPONSE generate(REQUEST request);
}
