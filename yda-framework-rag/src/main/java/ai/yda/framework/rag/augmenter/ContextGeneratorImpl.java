package ai.yda.framework.rag.augmenter;

import ai.yda.framework.rag.shared.model.RawContext;

public class ContextGeneratorImpl implements ContextGenerator {

    @Override
    public Context generateContext(final RawContext rawContext) {
        return new Context();
    }
}
