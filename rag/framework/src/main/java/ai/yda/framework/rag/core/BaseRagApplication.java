package ai.yda.framework.rag.core;

import ai.yda.framework.rag.augmenter.Augmenter;
import ai.yda.framework.rag.generator.Generator;
import ai.yda.framework.rag.model.RagContext;
import ai.yda.framework.rag.model.RagRawContext;
import ai.yda.framework.rag.model.RagRequest;
import ai.yda.framework.rag.model.RagResponse;
import ai.yda.framework.rag.retriever.Retriever;

public class BaseRagApplication extends AbstractRagApplication<RagRequest, RagRawContext, RagContext, RagResponse> {

    public BaseRagApplication(
            Retriever<RagRequest, RagRawContext> retriever,
            Augmenter<RagRequest, RagRawContext, RagContext> augmenter,
            Generator<RagRequest, RagContext, RagResponse> generator) {
        super(retriever, augmenter, generator);
    }
}
