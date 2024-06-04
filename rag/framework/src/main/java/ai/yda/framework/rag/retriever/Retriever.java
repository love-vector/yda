package ai.yda.framework.rag.retriever;

public interface Retriever<REQUEST, RAW_CONTEXT> {
    RAW_CONTEXT retrieve(REQUEST request);
}
