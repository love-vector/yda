package ai.yda.framework.rag.retriever.shared;

import lombok.Getter;
import lombok.Setter;

import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;

@Setter
@Getter
public class RetrieverProperties {

    private String databaseName = MilvusVectorStore.DEFAULT_DATABASE_NAME;

    private String collectionName = MilvusVectorStore.DEFAULT_COLLECTION_NAME;

    private Integer embeddingDimension = MilvusVectorStore.OPENAI_EMBEDDING_DIMENSION_SIZE;

    private Integer topK = SearchRequest.DEFAULT_TOP_K;

    private Boolean isProcessingEnabled = Boolean.FALSE;

    private String openAiKey;

    private String openAiModel;

    private String username;

    private String password;

    private String host;
}
