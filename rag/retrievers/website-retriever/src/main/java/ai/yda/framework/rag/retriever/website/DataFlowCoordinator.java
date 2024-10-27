package ai.yda.framework.rag.retriever.website;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.retriever.spliting.factory.ChunkingAlgorithm;
import ai.yda.framework.rag.core.transformators.factory.NodeTransformerFactory;
import ai.yda.framework.rag.core.transformators.pipline.PipelineAlgorithm;
import ai.yda.framework.rag.retriever.website.extractor.WebExtractor;
import ai.yda.framework.rag.retriever.website.indexing.WebsiteIndexing;
import org.springframework.lang.NonNull;

import java.util.Map;

public class DataFlowCoordinator {
    private final String datasource;
    private final WebsiteIndexing indexer;
    private final WebExtractor extractor;

    public DataFlowCoordinator(
            final @NonNull String datasource,
            final @NonNull WebsiteIndexing indexer,
            final @NonNull WebExtractor extractor,
            final @NonNull Boolean isProcessingEnabled,
            final @NonNull PipelineAlgorithm pipelineAlgorithm,
            final @NonNull ChunkingAlgorithm chunkingAlgorithm
    ) {
        this.datasource = datasource;
        this.indexer = indexer;
        this.extractor = extractor;

        if (Boolean.TRUE.equals(isProcessingEnabled)) {
            processAndIndexData(pipelineAlgorithm, chunkingAlgorithm);
        }
    }

    public void processAndIndexData(final PipelineAlgorithm pipelineAlgorithm, final ChunkingAlgorithm chunkingAlgorithm) {
        var nodeTransformerFactory = new NodeTransformerFactory();
        var nodeTransformerStrategy = nodeTransformerFactory.getStrategy(pipelineAlgorithm);
        var documentDataList = extractor.extract(datasource).stream().map(extractionResult -> new DocumentData(extractionResult.getContent(), Map.of("documentId", extractionResult.getUrl()))).toList();
        indexer.saveDocuments(nodeTransformerStrategy.processDataList(documentDataList, chunkingAlgorithm));
    }
}
