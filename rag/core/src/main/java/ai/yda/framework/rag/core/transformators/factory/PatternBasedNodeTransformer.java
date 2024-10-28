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
package ai.yda.framework.rag.core.transformators.factory;

import java.util.List;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.transformators.pipline.PipelineAlgorithm;

// TODO rename this class
public class PatternBasedNodeTransformer {
    private final NodeTransformerFactory nodeTransformerFactory;

    public PatternBasedNodeTransformer() {
        this.nodeTransformerFactory = new NodeTransformerFactory();
    }

    public List<DocumentData> nodeList(
            final List<DocumentData> documentDataList,
            ChunkingAlgorithm chunkingAlgorithm,
            PipelineAlgorithm pipelineAlgorithm) {
        var nodeTransformerStrategy = nodeTransformerFactory.getStrategy(pipelineAlgorithm);
        return nodeTransformerStrategy.processDataList(documentDataList, chunkingAlgorithm);
    }
}
