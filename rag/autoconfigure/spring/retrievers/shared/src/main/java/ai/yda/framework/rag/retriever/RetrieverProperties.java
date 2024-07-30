package ai.yda.framework.rag.retriever;

import lombok.Getter;
import lombok.Setter;

import org.springframework.ai.vectorstore.MilvusVectorStore;

@Setter
@Getter
public class RetrieverProperties {

    private String databaseName = MilvusVectorStore.DEFAULT_DATABASE_NAME;

    private String collectionName = MilvusVectorStore.DEFAULT_COLLECTION_NAME;

    private int embeddingDimension = MilvusVectorStore.OPENAI_EMBEDDING_DIMENSION_SIZE;

    private String openAiKey;

    private String openAiModel;

    private String username;

    private String password;

    private String host;
}
