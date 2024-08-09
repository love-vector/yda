package ai.yda.framework.rag.retriever.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.alibaba.fastjson.JSONObject;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.response.QueryResultsWrapper;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.util.Assert;

public class OptimizedMilvusVectorStore extends MilvusVectorStore {

    private static final int MAX_EMBEDDING_ARRAY_DIMENSIONS = 2048;

    private final MilvusServiceClient milvusClient;
    private final EmbeddingModel embeddingModel;
    private final String databaseName;
    private final String collectionName;
    private final boolean clearCollectionOnStartup;

    public OptimizedMilvusVectorStore(
            final MilvusServiceClient milvusClient,
            final EmbeddingModel embeddingModel,
            final MilvusVectorStoreConfig config,
            final boolean initializeSchema,
            final String collectionName,
            final String databaseName,
            final boolean clearCollectionOnStartup) {

        super(milvusClient, embeddingModel, config, initializeSchema);
        this.milvusClient = milvusClient;
        this.embeddingModel = embeddingModel;
        this.collectionName = collectionName;
        this.databaseName = databaseName;
        this.clearCollectionOnStartup = clearCollectionOnStartup;
    }

    @Override
    public void add(final List<Document> documents) {
        Assert.notNull(documents, "Documents must not be null");

        var docIdArray = new ArrayList<String>();
        var contentArray = new ArrayList<String>();
        var metadataArray = new ArrayList<JSONObject>();

        documents.forEach(document -> {
            docIdArray.add(document.getId());
            contentArray.add(document.getContent());
            metadataArray.add(new JSONObject(document.getMetadata()));
        });

        var embeddingArray = embedDocuments(List.copyOf(contentArray));

        var fields = List.of(
                new InsertParam.Field("doc_id", docIdArray),
                new InsertParam.Field("content", contentArray),
                new InsertParam.Field("metadata", metadataArray),
                new InsertParam.Field("embedding", embeddingArray));

        var insertParam = InsertParam.newBuilder()
                .withDatabaseName(this.databaseName)
                .withCollectionName(this.collectionName)
                .withFields(fields)
                .build();

        var status = this.milvusClient.insert(insertParam);
        if (status.getException() != null) {
            throw new RuntimeException("Failed to insert:", status.getException());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.clearCollectionOnStartup) {
            clearCollection();
        }
        super.afterPropertiesSet();
    }

    private List<List<Float>> embedDocuments(final List<String> documentsContent) {
        var embeddings = new ArrayList<List<Float>>();
        var totalSize = documentsContent.size();

        for (int start = 0; start < totalSize; start += MAX_EMBEDDING_ARRAY_DIMENSIONS) {
            var end = Math.min(start + MAX_EMBEDDING_ARRAY_DIMENSIONS, totalSize);
            var limitedDocumentContentSublist = documentsContent.subList(start, end);
            var embeddingsSublist = embeddingModel.embed(limitedDocumentContentSublist).stream()
                    .map(list -> list.stream().map(Number::floatValue).toList())
                    .toList();
            embeddings.addAll(embeddingsSublist);
        }

        return embeddings;
    }

    private void clearCollection() {
        if (isDatabaseCollectionExists()) {
            var allEntitiesIds = getAllEntitiesIds();
            delete(allEntitiesIds);
        }
    }

    private Boolean isDatabaseCollectionExists() {
        var collectionExistsResult = milvusClient.hasCollection(HasCollectionParam.newBuilder()
                .withDatabaseName(this.databaseName)
                .withCollectionName(this.collectionName)
                .build());
        if (collectionExistsResult.getException() != null) {
            throw new RuntimeException(
                    "Failed to check if database collection exists", collectionExistsResult.getException());
        }
        return collectionExistsResult.getData();
    }

    private List<String> getAllEntitiesIds() {
        var getAllIdsQueryResult = milvusClient.query(QueryParam.newBuilder()
                .withCollectionName(this.collectionName)
                .withExpr(DOC_ID_FIELD_NAME + " >= \"\"")
                .withOutFields(List.of(DOC_ID_FIELD_NAME))
                .build());

        if (getAllIdsQueryResult.getException() != null) {
            throw new RuntimeException("Failed to retrieve all entities ids", getAllIdsQueryResult.getException());
        }

        return Optional.ofNullable(getAllIdsQueryResult.getData())
                .map(data -> new QueryResultsWrapper(data)
                        .getFieldWrapper(DOC_ID_FIELD_NAME).getFieldData().parallelStream()
                                .map(Object::toString)
                                .toList())
                .orElse(Collections.emptyList());
    }
}
