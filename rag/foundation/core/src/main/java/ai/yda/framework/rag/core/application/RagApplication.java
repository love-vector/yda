package ai.yda.framework.rag.core.application;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.retriever.Retriever;

public interface RagApplication<REQUEST extends AssistantRequest, CONTEXT extends RagContext<?>, RESPONSE> {

    Retriever<REQUEST, CONTEXT> getRetriever();

    Augmenter<REQUEST, CONTEXT> getAugmenter();

    Generator<REQUEST, RESPONSE> getGenerator();

    RESPONSE doRag(REQUEST request);
}
