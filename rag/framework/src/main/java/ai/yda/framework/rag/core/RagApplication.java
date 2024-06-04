package ai.yda.framework.rag.core;

import ai.yda.framework.rag.augmenter.Augmenter;
import ai.yda.framework.rag.generator.Generator;
import ai.yda.framework.rag.retriever.Retriever;

public interface RagApplication<REQUEST, RAW_CONTEXT, CONTEXT, RESPONSE> {

    Retriever<REQUEST, RAW_CONTEXT> getRetriever();

    Augmenter<REQUEST, RAW_CONTEXT, CONTEXT> getAugmenter();

    Generator<REQUEST, CONTEXT, RESPONSE> getGenerator();

    default RESPONSE run(REQUEST request) {

        var rawContext = getRetriever().retrieve(request);
        var context = getAugmenter().augmentContext(request, rawContext);
        return getGenerator().generate(request, context);
    }
}
