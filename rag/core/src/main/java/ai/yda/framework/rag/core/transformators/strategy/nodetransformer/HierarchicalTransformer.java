package ai.yda.framework.rag.core.transformators.strategy.nodetransformer;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.retriever.spliting.factory.ChunkingAlgorithm;
import ai.yda.framework.rag.core.transformators.strategy.NodeTransformerStrategy;

import java.util.List;

public class HierarchicalTransformer implements NodeTransformerStrategy<DocumentData> {
    @Override
    public List<DocumentData> processDataList(List<DocumentData> dataList, ChunkingAlgorithm chunkingAlgorithm) {
        return null;
    }
    //TODO write second nodeTransformer

}
