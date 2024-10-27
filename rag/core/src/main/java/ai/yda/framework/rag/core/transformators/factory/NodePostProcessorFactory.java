package ai.yda.framework.rag.core.transformators.factory;

import ai.yda.framework.rag.core.transformators.pipline.PipelineAlgorithm;
import ai.yda.framework.rag.core.transformators.strategy.NodePostProcessorStrategy;
import ai.yda.framework.rag.core.transformators.strategy.postprocess.AutoMergingPostProcessor;
import ai.yda.framework.rag.core.transformators.strategy.postprocess.HierarchicalPostProcessor;

public class NodePostProcessorFactory {

    public NodePostProcessorStrategy getStrategy(final PipelineAlgorithm pipelineAlgorithm) {
        switch (pipelineAlgorithm) {
            case AUTO_MERGE:
                return new AutoMergingPostProcessor();
            case HIERARCHICAL:
                return new HierarchicalPostProcessor();
            default:
                throw new IllegalArgumentException("Unknown transformer algorithm: " + pipelineAlgorithm);
        }
    }
}
