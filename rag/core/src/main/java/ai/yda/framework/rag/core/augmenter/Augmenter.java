package ai.yda.framework.rag.core.augmenter;

import java.util.List;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;

/**
 * An Augmenter modifies or enriches the retrieved contexts to enhance the final response
 * generation. This can involve filtering, re-ranking, or adding new information.
 */
public interface Augmenter<REQUEST extends RagRequest, CONTEXT extends RagContext> {

    List<CONTEXT> augment(REQUEST request, List<CONTEXT> contexts);
}
