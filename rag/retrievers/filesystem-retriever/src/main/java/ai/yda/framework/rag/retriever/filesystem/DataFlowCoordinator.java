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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.lang.NonNull;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.RetrieverCoordinator;
import ai.yda.framework.rag.core.transformators.factory.ChunkingAlgorithm;
import ai.yda.framework.rag.core.transformators.factory.NodeTransformerFactory;
import ai.yda.framework.rag.core.transformators.pipline.PipelineAlgorithm;
import ai.yda.framework.rag.retriever.filesystem.extractor.service.FilesystemService;
import ai.yda.framework.rag.retriever.filesystem.indexing.FilesystemIndexing;
import ai.yda.framework.rag.retriever.filesystem.retriever.FilesystemRetriever;

public class DataFlowCoordinator implements RetrieverCoordinator<DocumentData> {
    private final String datasource;
    private final FilesystemIndexing indexer;

    private final PipelineAlgorithm pipelineAlgorithm;
    private final ChunkingAlgorithm chunkingAlgorithm;

    private final FilesystemRetriever filesystemRetriever;

    public DataFlowCoordinator(
            final @NonNull String datasource,
            final @NonNull FilesystemIndexing indexer,
            final @NonNull Boolean isProcessingEnabled,
            final @NonNull PipelineAlgorithm pipelineAlgorithm,
            final @NonNull ChunkingAlgorithm chunkingAlgorithm,
            final @NonNull FilesystemRetriever filesystemRetriever) {
        this.datasource = datasource;
        this.indexer = indexer;
        this.pipelineAlgorithm = pipelineAlgorithm;
        this.chunkingAlgorithm = chunkingAlgorithm;
        this.filesystemRetriever = filesystemRetriever;

        if (Boolean.TRUE.equals(isProcessingEnabled)) {
            index(process());
        }
    }

    @Override
    public List<DocumentData> process() {
        var nodeTransformerFactory = new NodeTransformerFactory();
        var nodeTransformerStrategy = nodeTransformerFactory.getStrategy(pipelineAlgorithm);
        var extractedData = new FilesystemService().extract(datasource);

        return nodeTransformerStrategy.processDataList(extractedData, chunkingAlgorithm);
    }

    @Override
    public void index(List<DocumentData> nodeList) {
        var documents = nodeList.stream()
                .map(documentData -> new Document(documentData.getContent(), documentData.getMetadata()))
                .collect(Collectors.toList());
        indexer.index(documents);
    }

    @Override
    public List<RagContext> retrieve(RagRequest request) {
        return filesystemRetriever.retrieve(request);
    }
}
