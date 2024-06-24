package ai.yda.framework.rag.core.application;

import reactor.core.publisher.Flux;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.common.shared.model.AssistantResponse;
import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.retriever.Retriever;

public interface RagApplication<
        REQUEST extends AssistantRequest, CONTEXT extends RagContext<?>, RESPONSE extends AssistantResponse> {

    Retriever<REQUEST, CONTEXT> getRetriever();

    Augmenter<REQUEST, CONTEXT> getAugmenter();

    Generator<REQUEST, RESPONSE> getGenerator();

    RESPONSE doRag(REQUEST request);

    default Flux<RESPONSE> doRagReactive(REQUEST request) {
        throw new RuntimeException("Reactive processing is not available for this RAG Application");
    }
}
