package ai.yda.framework.rag.base.store.rdbs;

import java.util.*;

public interface RelationalStore<T, ID> {

    T getById(ID id);

    T getByVectorId(String vectorId);

    Set<T> getAll();

    T save(T entity);

    void delete(T entity);
}
