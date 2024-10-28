package ai.yda.framework.rag.core.transformators.factory;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.transformators.pipline.PipelineAlgorithm;

import java.util.List;

//TODO rename this class
public class PatternBasedNodeTransformer {
    private final NodeTransformerFactory nodeTransformerFactory;

    public PatternBasedNodeTransformer() {
        this.nodeTransformerFactory = new NodeTransformerFactory();
    }

    public List<DocumentData> nodeList(final List<DocumentData> documentDataList, ChunkingAlgorithm chunkingAlgorithm, PipelineAlgorithm pipelineAlgorithm) {
        var nodeTransformerStrategy = nodeTransformerFactory.getStrategy(pipelineAlgorithm);
        return nodeTransformerStrategy.processDataList(documentDataList, chunkingAlgorithm);
    }
}
