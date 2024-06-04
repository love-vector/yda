package ai.yda.framework.augmenter;

import ai.yda.framework.rag.augmenter.BaseAugmenter;
import ai.yda.framework.rag.model.RagContext;
import ai.yda.framework.rag.model.RagRequest;

public class CommonAugmenter extends BaseAugmenter {

    @Override
    public RagContext augmentContext(RagRequest ragRequest, RagContext context) {
        return null;
    }
}
