package ai.yda.rag;

import ai.yda.framework.augmenter.CommonAugmenter;
import ai.yda.framework.generator.CommonGenerator;
import ai.yda.framework.rag.augmenter.Augmenter;
import ai.yda.framework.rag.core.BaseRagApplication;
import ai.yda.framework.rag.dto.RagContext;
import ai.yda.framework.rag.dto.RagRawContext;
import ai.yda.framework.rag.dto.RagRequest;
import ai.yda.framework.rag.dto.RagResponse;
import ai.yda.framework.rag.generator.Generator;
import ai.yda.framework.rag.retriever.Retriever;
import ai.yda.framework.retriever.CommonRetriever;

public class RagApp extends BaseRagApplication {

    @Override
    public Retriever<RagRequest, RagRawContext> getRetriever() {
        return new CommonRetriever();
    }

    @Override
    public Augmenter<RagRequest, RagRawContext, RagContext> getAugmenter() {
        return new CommonAugmenter();
    }

    @Override
    public Generator<RagRequest, RagContext, RagResponse> getGenerator() {
        return new CommonGenerator();
    }
}
