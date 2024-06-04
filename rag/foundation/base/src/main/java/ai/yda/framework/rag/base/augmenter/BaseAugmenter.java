package ai.yda.framework.rag.base.augmenter;

import ai.yda.framework.rag.base.model.BaseRagContext;
import ai.yda.framework.rag.base.model.BaseRagRequest;
import ai.yda.framework.rag.core.augmenter.Augmenter;

public class BaseAugmenter implements Augmenter<BaseRagRequest, BaseRagContext> {

    @Override
    public BaseRagContext augment(BaseRagRequest request, BaseRagContext context) {
        return context;
    }
}
