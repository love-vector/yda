package ai.yda.framework.rag.base.augmenter;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;

public class BaseAugmenter implements Augmenter<BaseAssistantRequest, BaseRagContext> {

    @Override
    public BaseAssistantRequest augment(BaseAssistantRequest request, BaseRagContext context) {
        request.setContext(context.getKnowledge().toString());
        return request;
    }
}
