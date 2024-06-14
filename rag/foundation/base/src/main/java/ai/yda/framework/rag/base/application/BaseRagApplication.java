package ai.yda.framework.rag.base.application;

import ai.yda.framework.rag.base.model.BaseRagContext;
import ai.yda.framework.rag.base.model.BaseRagRequest;
import ai.yda.framework.rag.base.model.BaseRagResponse;
import ai.yda.framework.rag.core.application.RagApplication;
import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.retriever.Retriever;

public class BaseRagApplication extends RagApplication<BaseRagRequest, BaseRagContext, BaseRagResponse> {

    private Retriever<BaseRagRequest, BaseRagContext> retriever;
    private Augmenter<BaseRagRequest, BaseRagContext> augmenter;
    private Generator<BaseRagRequest, BaseRagResponse> generator;

    public BaseRagApplication(
            Retriever<BaseRagRequest, BaseRagContext> retriever,
            Augmenter<BaseRagRequest, BaseRagContext> augmenter,
            Generator<BaseRagRequest, BaseRagResponse> generator) {
        super(retriever, augmenter, generator);
    }
}
