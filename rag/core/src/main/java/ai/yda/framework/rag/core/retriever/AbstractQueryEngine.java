package ai.yda.framework.rag.core.retriever;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.chunking.model.DocumentData;
import ai.yda.framework.rag.core.retriever.chunking.strategy.ChunkStrategy;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractQueryEngine {
    private final Retriever<RagRequest, RagContext> retriever;
    private final Indexer<DocumentData> indexer;
    private final DataExtractor<Set<DocumentData>> dataExtractor;

    private final ChunkStrategy chunkStrategy;

    public AbstractQueryEngine(Retriever retriever, Indexer indexer, DataExtractor dataExtractor, ChunkStrategy chunkStrategy) {
        this.retriever = retriever;
        this.indexer = indexer;
        this.dataExtractor = dataExtractor;
        this.chunkStrategy = chunkStrategy;
    }

    public void process(String source) {
        var extracted = dataExtractor.extract(source).stream().map(value -> new DocumentData(value.getContent(),value.getMetadata())).collect(Collectors.toList());
        var chunkData = chunkStrategy.splitChunks(extracted);
        indexer.index();
    }

}
