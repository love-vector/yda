package ai.yda.framework.rag.base.store.vector;

import ai.yda.framework.rag.base.exception.CollectionDoesNotExistException;

public interface VectorStore extends org.springframework.ai.vectorstore.VectorStore {

    void createCollection(String collectionName) throws CollectionDoesNotExistException;

    void deleteCollection(String collectionName) throws CollectionDoesNotExistException;
}
