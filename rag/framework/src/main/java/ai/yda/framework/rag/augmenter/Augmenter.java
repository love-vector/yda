package ai.yda.framework.rag.augmenter;

public interface Augmenter<REQUEST, CONTEXT> {

    CONTEXT augmentContext(REQUEST request, CONTEXT context);
}
