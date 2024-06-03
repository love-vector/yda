package ai.yda.framework.retriever;

import ai.yda.framework.rag.retriever.Retriever;
import ai.yda.framework.rag.shared.model.Request;
import ai.yda.framework.rag.shared.model.RawContext;

public class IntentBasedRetriever implements Retriever<Request, RawContext> {

    @Override
    public RawContext retrieve(Request request) {
        return null;
    }
}
