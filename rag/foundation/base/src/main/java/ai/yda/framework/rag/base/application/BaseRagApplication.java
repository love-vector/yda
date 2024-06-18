package ai.yda.framework.rag.base.application;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.rag.core.application.AbstractRagApplication;
import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;

public class BaseRagApplication
        extends AbstractRagApplication<BaseAssistantRequest, BaseRagContext, BaseAssistantResponse> {

    public BaseRagApplication(
            Retriever<BaseAssistantRequest, BaseRagContext> retriever,
            Augmenter<BaseAssistantRequest, BaseRagContext> augmenter,
            Generator<BaseAssistantRequest, BaseAssistantResponse> generator) {
        super(retriever, augmenter, generator);
    }
}
