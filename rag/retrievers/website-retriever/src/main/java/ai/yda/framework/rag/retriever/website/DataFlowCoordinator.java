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
package ai.yda.framework.rag.retriever.website;

import java.util.List;
import java.util.Map;
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
import ai.yda.framework.rag.retriever.website.extractor.WebExtractor;
import ai.yda.framework.rag.retriever.website.indexing.WebsiteIndexing;
import ai.yda.framework.rag.retriever.website.retriever.WebsiteRetriever;

public class DataFlowCoordinator implements RetrieverCoordinator<DocumentData> {
    private final String datasource;
    private final WebsiteIndexing indexer;
    private final WebExtractor extractor;

    private final WebsiteRetriever websiteRetriever;

    private final PipelineAlgorithm pipelineAlgorithm;
    private final ChunkingAlgorithm chunkingAlgorithm;

    public DataFlowCoordinator(
            final @NonNull String datasource,
            final @NonNull WebsiteIndexing indexer,
            final @NonNull WebExtractor extractor,
            final @NonNull Boolean isProcessingEnabled,
            final @NonNull WebsiteRetriever websiteRetriever,
            final @NonNull PipelineAlgorithm pipelineAlgorithm,
            final @NonNull ChunkingAlgorithm chunkingAlgorithm) {
        this.datasource = datasource;
        this.indexer = indexer;
        this.extractor = extractor;
        this.websiteRetriever = websiteRetriever;
        this.pipelineAlgorithm = pipelineAlgorithm;
        this.chunkingAlgorithm = chunkingAlgorithm;

        if (Boolean.TRUE.equals(isProcessingEnabled)) {
            index(process());
        }
    }

    @Override
    public List<DocumentData> process() {
        var nodeTransformerFactory = new NodeTransformerFactory();
        var nodeTransformerStrategy = nodeTransformerFactory.getStrategy(pipelineAlgorithm);
        var extractedData = extractor.extract(datasource);
        var documentData = extractedData.stream()
                .map(extractionResult -> new DocumentData(
                        extractionResult.getContent(), Map.of("documentId", extractionResult.getUrl())))
                .collect(Collectors.toList());

        return nodeTransformerStrategy.processDataList(documentData, chunkingAlgorithm);
    }

    @Override
    public void index(List<DocumentData> nodeList) {
        var documents = nodeList.stream()
                .map(documentData -> new Document(documentData.getContent(), documentData.getMetadata()))
                .toList();
        indexer.index(documents);
    }

    @Override
    public List<RagContext> retrieve(RagRequest request) {
        return websiteRetriever.retrieve(request);
    }
}
