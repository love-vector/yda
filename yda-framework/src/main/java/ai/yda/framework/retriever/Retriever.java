package ai.yda.framework.retriever;

public interface Retriever<T, M> {
    T retrieve(M request);

}
