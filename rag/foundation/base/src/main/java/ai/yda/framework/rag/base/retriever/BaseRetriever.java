package ai.yda.framework.rag.base.retriever;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;

@RequiredArgsConstructor
public class BaseRetriever implements Retriever<BaseAssistantRequest, BaseRagContext> {

    private final VectorStore vectorStore;

    @Override
    public BaseRagContext retrieve(BaseAssistantRequest request) {
        var documents = vectorStore.similaritySearch(request.getQuery());
        var chunks = documents.stream().map(Document::getContent).toList();
        return BaseRagContext.builder().knowledge(chunks).build();
    }
}
