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
package ai.yda.framework.rag.retriever.filesystem;

import org.springframework.lang.NonNull;

import ai.yda.framework.rag.core.transformators.factory.ChunkingAlgorithm;
import ai.yda.framework.rag.core.transformators.factory.NodeTransformerFactory;
import ai.yda.framework.rag.core.transformators.pipline.PipelineAlgorithm;
import ai.yda.framework.rag.retriever.filesystem.extractor.service.FilesystemService;
import ai.yda.framework.rag.retriever.filesystem.indexing.FilesystemIndexing;

public class DataFlowCoordinator {
    private final String datasource;
    private final FilesystemIndexing indexer;

    public DataFlowCoordinator(
            final @NonNull String datasource,
            final @NonNull FilesystemIndexing indexer,
            final @NonNull Boolean isProcessingEnabled,
            final @NonNull PipelineAlgorithm pipelineAlgorithm,
            final @NonNull ChunkingAlgorithm chunkingAlgorithm) {
        this.datasource = datasource;
        this.indexer = indexer;

        if (Boolean.TRUE.equals(isProcessingEnabled)) {
            processAndIndexData(pipelineAlgorithm, chunkingAlgorithm);
        }
    }

    public void processAndIndexData(
            final PipelineAlgorithm pipelineAlgorithm, final ChunkingAlgorithm chunkingAlgorithm) {
        var nodeTransformerFactory = new NodeTransformerFactory();
        var nodeTransformerStrategy = nodeTransformerFactory.getStrategy(pipelineAlgorithm);
        var documentDataList = new FilesystemService().extract(datasource);
        var processedDocumentDataList = nodeTransformerStrategy.processDataList(documentDataList, chunkingAlgorithm);

        indexer.saveDocuments(processedDocumentDataList);
    }
}
