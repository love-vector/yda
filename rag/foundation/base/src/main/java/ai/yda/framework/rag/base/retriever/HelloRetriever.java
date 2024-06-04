package ai.yda.framework.rag.base.retriever;

import ai.yda.framework.rag.base.store.vector.VectorStore;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.Retriever;

public class HelloRetriever implements Retriever<RagRequest, RagContext> {

    private VectorStore vectorStore;

    @Override
    public RagContext retrieve(RagRequest request) {

        return null;
    }
}
