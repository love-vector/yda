package ai.yda.framework.rag.core.application;

import java.util.List;

import lombok.AllArgsConstructor;

import ai.yda.common.shared.model.AssistantRequest;
import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.retriever.Retriever;

@AllArgsConstructor
public abstract class AbstractRagApplication<REQUEST extends AssistantRequest, CONTEXT extends RagContext<?>, RESPONSE>
        implements RagApplication<REQUEST, CONTEXT, RESPONSE> {

    private List<Retriever<REQUEST, CONTEXT>> retrievers;
    private Augmenter<REQUEST, CONTEXT> augmenter;
    private Generator<REQUEST, RESPONSE> generator;

    @Override
    public RESPONSE doRag(final REQUEST request) {
        var rawContextList = retrievers.parallelStream()
                .map(retriever -> retriever.retrieve(request))
                .toList();
        var requestWithContext = augmenter.augment(request, rawContextList);
        return generator.generate(requestWithContext);
    }
}
