package ai.yda.framework.rag.base.store;

public interface VectorStore extends org.springframework.ai.vectorstore.VectorStore {

    void createCollection(String collectionName);

    void deleteCollection(String collectionName);
}
