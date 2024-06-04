package ai.yda.framework.rag.core;

import lombok.RequiredArgsConstructor;

import ai.yda.framework.rag.augmenter.Augmenter;
import ai.yda.framework.rag.generator.Generator;
import ai.yda.framework.rag.model.RagContext;
import ai.yda.framework.rag.model.RagRequest;
import ai.yda.framework.rag.model.RagResponse;
import ai.yda.framework.rag.retriever.Retriever;

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
