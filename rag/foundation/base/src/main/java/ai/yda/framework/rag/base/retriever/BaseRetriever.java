package ai.yda.framework.rag.base.retriever;

import org.springframework.ai.document.Document;

import ai.yda.framework.rag.base.model.BaseRagContext;
import ai.yda.framework.rag.base.store.VectorStore;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.Retriever;

public class BaseRetriever implements Retriever<RagRequest, RagContext> {

    private VectorStore vectorStore;

    @Override
    public RagContext retrieve(RagRequest request) {
        var documents = vectorStore.similaritySearch(request.getContent());
        var chunks = documents.stream().map(Document::getContent).toList();
        return BaseRagContext.builder().chunks(chunks).build();
    }
}
