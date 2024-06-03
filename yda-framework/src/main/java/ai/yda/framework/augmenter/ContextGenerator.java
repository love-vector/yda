package ai.yda.framework.augmenter;

import ai.yda.framework.shared.model.RawContext;

public interface ContextGenerator {

    Context generateContext(RawContext rawContext);
}
