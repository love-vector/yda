package ai.yda.framework.rag.base.retriever;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;

public class WebSiteRetriever implements Retriever<BaseAssistantRequest, BaseRagContext> {
    @Override
    public BaseRagContext retrieve(BaseAssistantRequest request) {
        return null;
    }
}
