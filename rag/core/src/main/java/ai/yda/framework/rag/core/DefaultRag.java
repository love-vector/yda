package ai.yda.framework.rag.core;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.util.StringUtil;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class DefaultRag implements Rag<RagRequest, RagResponse> {

    private final List<Retriever<RagRequest, RagContext>> retrievers;

    private final List<Augmenter<RagRequest, RagContext>> augmenters;

    private final Generator<RagRequest, RagResponse> generator;

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
