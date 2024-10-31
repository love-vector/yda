/*
 * YDA - Open-Source Java AI Assistant.
 * Copyright (C) 2024 Love Vector OÜ <https://vector-inc.dev/>

 * This file is part of YDA.

 * YDA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * YDA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with YDA.  If not, see <https://www.gnu.org/licenses/>.
*/
package ai.yda.framework.rag.core.transformators.strategy.nodetransformer;

import java.util.ArrayList;
import java.util.List;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.model.Node;
import ai.yda.framework.rag.core.transformators.factory.ChunkingAlgorithm;
import ai.yda.framework.rag.core.transformators.factory.PatternBasedChunking;
import ai.yda.framework.rag.core.transformators.strategy.NodeTransformerStrategy;

public class HierarchicalTransformer implements NodeTransformerStrategy<DocumentData> {
    @Override
    public List<DocumentData> processDataList(
            final List<DocumentData> dataList, final ChunkingAlgorithm chunkingAlgorithm) {
        var nodeList = new PatternBasedChunking().nodeList(chunkingAlgorithm, dataList);
        var hierarchNodes = processNodeHierarchy(nodeList);
        return null;
    }

    private List<DocumentData> processNodeHierarchy(final List<Node> nodeList) {
        List<DocumentData> hierarchicalChunks = new ArrayList<>();
        nodeList.forEach(node -> {});

        return hierarchicalChunks;
    }
}
