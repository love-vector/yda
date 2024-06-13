package ai.yda.framework.rag.base.application;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.rag.base.model.BaseRagContext;
import ai.yda.framework.rag.core.application.AbstractRagApplication;
import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.retriever.Retriever;

public class BaseRagApplication
        extends AbstractRagApplication<BaseAssistantRequest, BaseRagContext, BaseAssistantResponse> {

    private Retriever<BaseAssistantRequest, BaseRagContext> retriever;
    private Augmenter<BaseAssistantRequest, BaseRagContext> augmenter;
    private Generator<BaseAssistantRequest, BaseRagContext, BaseAssistantResponse> generator;

    public BaseRagApplication(
            Retriever<BaseAssistantRequest, BaseRagContext> retriever,
            Augmenter<BaseAssistantRequest, BaseRagContext> augmenter,
            Generator<BaseAssistantRequest, BaseRagContext, BaseAssistantResponse> generator) {
        super(retriever, augmenter, generator);
    }
}
