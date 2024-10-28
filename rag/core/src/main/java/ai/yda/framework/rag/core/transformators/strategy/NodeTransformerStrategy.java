package ai.yda.framework.rag.core.transformators.strategy;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.transformators.factory.ChunkingAlgorithm;

import java.util.List;

public interface NodeTransformerStrategy<NODES extends DocumentData> {
    List<NODES> processDataList(List<NODES> dataList, ChunkingAlgorithm chunkingAlgorithm);
}
