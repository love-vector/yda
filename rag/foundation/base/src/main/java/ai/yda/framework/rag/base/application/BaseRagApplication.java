package ai.yda.framework.rag.base.application;

import java.util.List;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.application.AbstractRagApplication;
import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;

public class BaseRagApplication<RESPONSE>
        extends AbstractRagApplication<BaseAssistantRequest, BaseRagContext, RESPONSE> {

    public BaseRagApplication(
            List<Retriever<BaseAssistantRequest, BaseRagContext>> retrievers,
            Augmenter<BaseAssistantRequest, BaseRagContext> augmenter,
            Generator<BaseAssistantRequest, RESPONSE> generator) {
        super(retrievers, augmenter, generator);
    }
}
