package ai.yda.framework.rag.base.retriever;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import ai.yda.framework.rag.base.model.BaseRagContext;
import ai.yda.framework.rag.base.model.BaseRagRequest;
import ai.yda.framework.rag.core.retriever.Retriever;

@RequiredArgsConstructor
public class BaseRetriever implements Retriever<BaseRagRequest, BaseRagContext> {

    private final VectorStore vectorStore;

    @Override
    public BaseRagContext retrieve(BaseRagRequest request) {
        var documents = vectorStore.similaritySearch(request.getContent());
        var chunks = documents.stream().map(Document::getContent).toList();
        return BaseRagContext.builder().chunks(chunks).build();
    }
}
