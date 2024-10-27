package ai.yda.framework.rag.core.transformators.factory;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.transformators.pipline.PipelineAlgorithm;
import ai.yda.framework.rag.core.transformators.strategy.NodeTransformerStrategy;
import ai.yda.framework.rag.core.transformators.strategy.nodetransformer.AutoMergingTransformer;
import ai.yda.framework.rag.core.transformators.strategy.nodetransformer.HierarchicalTransformer;

public class NodeTransformerFactory {

    public NodeTransformerStrategy<DocumentData> getStrategy(final PipelineAlgorithm pipelineAlgorithm) {
        switch (pipelineAlgorithm) {
            case AUTO_MERGE:
                return new AutoMergingTransformer();
            case HIERARCHICAL:
                return new HierarchicalTransformer();
            default:
                throw new IllegalArgumentException("Unknown transformer algorithm: " + pipelineAlgorithm);
        }
    }
}
