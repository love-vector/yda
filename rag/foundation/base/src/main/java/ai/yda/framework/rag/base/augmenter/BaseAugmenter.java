package ai.yda.framework.rag.base.augmenter;

import java.util.List;
import java.util.stream.Collectors;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;

public class BaseAugmenter implements Augmenter<BaseAssistantRequest, BaseRagContext> {

    private static final String CONTEXT_DELIMITER = ".";

    @Override
    public BaseAssistantRequest augment(final BaseAssistantRequest request, final List<BaseRagContext> contexts) {
        var context = contexts.parallelStream()
                .map(ragContext -> String.join(CONTEXT_DELIMITER, ragContext.getKnowledge()))
                .collect(Collectors.joining(CONTEXT_DELIMITER));
        return request.toBuilder().context(context).build();
    }
}
