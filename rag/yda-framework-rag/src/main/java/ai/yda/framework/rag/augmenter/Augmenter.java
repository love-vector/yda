package ai.yda.framework.rag.augmenter;

public interface Augmenter<REQUEST, RAW_CONTEXT, CONTEXT> {

    CONTEXT augmentContext(REQUEST request, RAW_CONTEXT rawContext);
}
