package ai.yda.framework.rag.shared.store.vector;

public interface VectorStore extends org.springframework.ai.vectorstore.VectorStore {

    void createCollection(String collectionName) throws CollectionDoesNotExistException;

    void deleteCollection(String collectionName) throws CollectionDoesNotExistException;
}
