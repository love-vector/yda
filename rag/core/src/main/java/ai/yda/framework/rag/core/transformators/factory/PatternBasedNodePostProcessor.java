package ai.yda.framework.rag.core.transformators.factory;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.transformators.pipline.PipelineAlgorithm;
import ai.yda.framework.rag.core.transformators.strategy.NodePostProcessorStrategy;

import java.util.List;

//TODO rename this class
public class PatternBasedNodePostProcessor {
    private final NodePostProcessorFactory nodePostProcessorFactory;

    public PatternBasedNodePostProcessor() {
        this.nodePostProcessorFactory = new NodePostProcessorFactory();
    }

    public List<RagContext> ragContextList(final List<RagContext> ragContext, final PipelineAlgorithm pipelineAlgorithm) {
        NodePostProcessorStrategy nodePostProcessorStrategy = nodePostProcessorFactory.getStrategy(pipelineAlgorithm);
        return nodePostProcessorStrategy.retrieveRagContext(ragContext);
    }
}
