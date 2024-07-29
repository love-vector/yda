package ai.yda.framework.rag.core.application;

import java.util.List;
import java.util.stream.Collectors;

import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.util.StringUtil;

public class DefaultRagApplication implements RagApplication<RagRequest, RagResponse> {

    protected final List<Retriever<RagRequest, RagContext>> retrievers;

    protected final List<Augmenter<RagRequest, RagContext>> augmenters;

    protected final Generator<RagRequest, RagResponse> generator;

    public DefaultRagApplication(
            final List<Retriever<RagRequest, RagContext>> retrievers,
            final List<Augmenter<RagRequest, RagContext>> augmenters,
            final Generator<RagRequest, RagResponse> generator) {
        this.retrievers = retrievers;
        this.augmenters = augmenters;
        this.generator = generator;
    }

    @Override
    public RagResponse doRag(final RagRequest request) {
        var contexts = retrievers.parallelStream()
                .map(retriever -> retriever.retrieve(request))
                .toList();
        for (var augmenter : augmenters) {
            contexts = augmenter.augment(request, contexts);
        }
        return generator.generate(request, mergeContexts(contexts));
    }

    protected String mergeContexts(final List<RagContext> contexts) {
        return contexts.parallelStream()
                .map(ragContext -> String.join(StringUtil.POINT, ragContext.getKnowledge()))
                .collect(Collectors.joining(StringUtil.POINT));
    }
}
