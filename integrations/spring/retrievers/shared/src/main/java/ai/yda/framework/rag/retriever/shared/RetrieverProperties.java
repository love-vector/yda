package ai.yda.framework.rag.retriever.shared;

import lombok.Getter;
import lombok.Setter;

import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;

@Setter
@Getter
public class RetrieverProperties {

    private String collectionName = MilvusVectorStore.DEFAULT_COLLECTION_NAME;

    private Integer topK = SearchRequest.DEFAULT_TOP_K;

    private Boolean isProcessingEnabled = Boolean.FALSE;

    private Boolean clearCollectionOnStartup = Boolean.FALSE;
}
