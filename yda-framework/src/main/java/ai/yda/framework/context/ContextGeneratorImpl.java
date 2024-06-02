package ai.yda.framework.context;

import ai.yda.framework.shared.model.RawContext;

public class ContextGeneratorImpl implements ContextGenerator {

    @Override
    public Context generateContext(final RawContext rawContext) {
        return new Context();
    }
}
