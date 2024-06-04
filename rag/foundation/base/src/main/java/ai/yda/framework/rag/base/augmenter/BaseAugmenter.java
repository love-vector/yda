package ai.yda.framework.rag.base.augmenter;

import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;

public class BaseAugmenter implements Augmenter<RagRequest, RagContext> {

    @Override
    public RagContext augment(RagRequest request, RagContext context) {
        return context;
    }
}
