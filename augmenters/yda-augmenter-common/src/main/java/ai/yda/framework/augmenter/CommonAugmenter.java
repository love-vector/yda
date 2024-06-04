package ai.yda.framework.augmenter;

import ai.yda.framework.rag.augmenter.BaseAugmenter;
import ai.yda.framework.rag.dto.RagContext;
import ai.yda.framework.rag.dto.RagRawContext;
import ai.yda.framework.rag.dto.RagRequest;

public class CommonAugmenter extends BaseAugmenter {

    @Override
    public RagContext augmentContext(RagRequest ragRequest, RagRawContext ragRawContext) {
        return null;
    }
}
