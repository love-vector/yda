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

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.transformators.pipline.PipelineAlgorithm;
import ai.yda.framework.rag.core.transformators.strategy.NodePostProcessorStrategy;

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
