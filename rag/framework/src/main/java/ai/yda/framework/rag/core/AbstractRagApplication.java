package ai.yda.framework.rag.core;

import lombok.RequiredArgsConstructor;

import ai.yda.framework.rag.augmenter.Augmenter;
import ai.yda.framework.rag.generator.Generator;
import ai.yda.framework.rag.retriever.Retriever;

@RequiredArgsConstructor
public abstract class AbstractRagApplication<REQUEST, RAW_CONTEXT, CONTEXT, RESPONSE> {

    private final Retriever<REQUEST, RAW_CONTEXT> retriever;

    private final Augmenter<REQUEST, RAW_CONTEXT, CONTEXT> augmenter;

    private final Generator<REQUEST, CONTEXT, RESPONSE> generator;

    public RESPONSE run(REQUEST request) {
        var rawContext = retriever.retrieve(request);
        var context = augmenter.augmentContext(request, rawContext);
        return generator.generate(request, context);
    }
}
