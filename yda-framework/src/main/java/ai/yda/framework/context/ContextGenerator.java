package ai.yda.framework.context;

import ai.yda.framework.shared.model.RawContext;

public interface ContextGenerator {

    Context generateContext(RawContext rawContext);
}
