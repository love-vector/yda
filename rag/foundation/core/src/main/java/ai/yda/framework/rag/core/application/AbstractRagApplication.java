package ai.yda.framework.rag.core.application;

import lombok.AllArgsConstructor;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.common.shared.model.AssistantResponse;
import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.retriever.Retriever;

@AllArgsConstructor
public abstract class AbstractRagApplication<
                REQUEST extends AssistantRequest, CONTEXT extends RagContext, RESPONSE extends AssistantResponse>
        implements RagApplication<REQUEST, CONTEXT, RESPONSE> {

    private Retriever<REQUEST, CONTEXT> retriever;
    private Augmenter<REQUEST, CONTEXT> augmenter;
    private Generator<REQUEST, CONTEXT, RESPONSE> generator;

    @Override
    public Retriever<REQUEST, CONTEXT> getRetriever() {
        return retriever;
    }

    @Override
    public Augmenter<REQUEST, CONTEXT> getAugmenter() {
        return augmenter;
    }

    @Override
    public Generator<REQUEST, CONTEXT, RESPONSE> getGenerator() {
        return generator;
    }

    @Override
    public RESPONSE doRag(REQUEST request) {
        var rawContext = retriever.retrieve(request);
        var context = augmenter.augment(request, rawContext);
        return generator.generate(request, context);
    }
}
