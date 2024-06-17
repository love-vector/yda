package ai.yda.framework.rag.base.augmenter;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.base.model.BaseRagContext;
import ai.yda.framework.rag.core.augmenter.Augmenter;

public class BaseAugmenter implements Augmenter<BaseAssistantRequest, BaseRagContext> {

    @Override
    public BaseRagContext augment(BaseAssistantRequest request, BaseRagContext context) {
        return context;
    }
}
