package ai.yda.framework.rag.core.generator;

import ai.yda.common.shared.model.AssistantRequest;

public abstract class AbstractGenerator<REQUEST extends AssistantRequest, RESPONSE>
        implements Generator<REQUEST, RESPONSE> {}
