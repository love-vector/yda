package ai.yda.framework.rag.core.application;

import lombok.RequiredArgsConstructor;

import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.retriever.Retriever;

@RequiredArgsConstructor
public abstract class AbstractRagApplication<
        REQUEST extends RagRequest, CONTEXT extends RagContext, RESPONSE extends RagResponse> {

    private final Retriever<REQUEST, CONTEXT> retriever;

    private final Augmenter<REQUEST, CONTEXT> augmenter;

    private final Generator<REQUEST, CONTEXT, RESPONSE> generator;

    public RESPONSE doRag(REQUEST request) {
        var rawContext = retriever.retrieve(request);
        var context = augmenter.augment(request, rawContext);
        return generator.generate(request, context);
    }
}
