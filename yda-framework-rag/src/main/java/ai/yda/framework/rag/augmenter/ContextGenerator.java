package ai.yda.framework.rag.augmenter;

import ai.yda.framework.rag.shared.model.RawContext;

public interface ContextGenerator {

    Context generateContext(RawContext rawContext);
}
