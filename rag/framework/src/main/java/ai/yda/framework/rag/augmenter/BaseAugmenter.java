package ai.yda.framework.rag.augmenter;

import ai.yda.framework.rag.model.RagContext;
import ai.yda.framework.rag.model.RagRawContext;
import ai.yda.framework.rag.model.RagRequest;

public abstract class BaseAugmenter implements Augmenter<RagRequest, RagRawContext, RagContext> {}
