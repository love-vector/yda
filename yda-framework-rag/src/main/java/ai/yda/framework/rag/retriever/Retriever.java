package ai.yda.framework.rag.retriever;

public interface Retriever<T, M> {
    T retrieve(M request);

}
