package ai.yda.framework.shared.store.vector;

import java.util.List;

public interface VectorStore<T> {

    Document createIntent(T intent);

    void deleteIntent(T intent);

    List<Document> search(String message);

    void createCollection(String name) throws CollectionDoesNotExistException;

    void deleteCollection(String name) throws CollectionDoesNotExistException;
}
