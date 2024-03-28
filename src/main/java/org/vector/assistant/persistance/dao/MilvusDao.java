package org.vector.assistant.persistance.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.milvus.client.MilvusClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.SearchResultData;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.*;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.vector.assistant.util.constant.MilvusConstant;
import org.vector.assistant.util.converter.EmbeddingConverter;

@Slf4j
@Service
@RequiredArgsConstructor
public class MilvusDao {

    @Value("${org.vector.milvus.topK}")
    private Integer milvusTopK;

    @Value("${org.vector.milvus.chunkFieldDimension}")
    private Integer chunkFieldDimension;

    private final MilvusClient milvusClient;

    public void checkHealth() {
        log.debug("Checking milvus connection: {}", milvusClient.checkHealth());
    }

    public SearchResultData searchVectors(final List<Double> queryVector, final String collectionName) {
        return this.search(milvusClient, queryVector, collectionName);
    }

    public SearchResultData searchVectors(
            final List<Double> queryVector, final Integer topk, final String collectionName) {
        return this.search(milvusClient, queryVector, collectionName, topk);
    }

    public long flushVectorToMilvus(final List<Double> vector, final String collectionName) {
        this.createCollectionIfNotExist(milvusClient, collectionName);
        var insertedVectorId = insertVector(milvusClient, collectionName, vector);
        flush(milvusClient, collectionName);

        return insertedVectorId;
    }

    public long deleteVectorInCollection(final Long vectorId, final String collectionName) {
        String deleteExpression = String.format("%s in [%s]", MilvusConstant.RELATION_ID, vectorId);
        milvusClient.delete(DeleteParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr(deleteExpression)
                .build());
        log.debug("Search finished: {}", vectorId);
        return vectorId;
    }

    private void createCollectionIfNotExist(final MilvusClient client, final String collectionName) {
        log.debug("Checking if collection {} exists", collectionName);
        var collectionExists = client.hasCollection(HasCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());

        if (!collectionExists.getStatus().equals(R.Status.Success.getCode())) {
            log.error("Milvus connection failed: {}", collectionExists);

            throw new RuntimeException(collectionExists.getException());
        }

        if (!collectionExists.getData()) {
            client.createCollection(CreateCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withFieldTypes(new ArrayList<>() {
                        {
                            add(FieldType.newBuilder()
                                    .withName(MilvusConstant.VECTOR_FIELD)
                                    .withDimension(chunkFieldDimension)
                                    .withDataType(DataType.FloatVector)
                                    .build());
                            add(FieldType.newBuilder()
                                    .withName(MilvusConstant.COLLECTION_FIELD_NAME)
                                    .withDataType(DataType.VarChar)
                                    .withMaxLength(30)
                                    .build());
                            add(FieldType.newBuilder()
                                    .withName(MilvusConstant.RELATION_ID)
                                    .withDataType(DataType.Int64)
                                    .withPrimaryKey(Boolean.TRUE)
                                    .withAutoID(true)
                                    .build());
                        }
                    })
                    .build());
            createIndex(client, collectionName);
            log.debug("Created collection: {}", collectionName);
        }
    }

    private SearchResultData search(
            final MilvusClient client, final List<Double> queryVector, final String collectionName) {
        try {
            this.loadCollection(client, collectionName);
            var floatVector = EmbeddingConverter.normalizeVector(queryVector);
            var searched = client.search(SearchParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withMetricType(MetricType.COSINE)
                    .withVectorFieldName(MilvusConstant.VECTOR_FIELD)
                    .withTopK(this.milvusTopK)
                    .withOutFields(List.of(MilvusConstant.COLLECTION_FIELD_NAME))
                    .withVectors(Collections.singletonList(floatVector))
                    .build());

            if (searched.getStatus() != R.Status.Success.getCode()) {
                log.error("Search failed: {}", searched);

                throw new RuntimeException(searched.getException());
            }

            var data = searched.getData().getResults();
            log.debug("Search finished: {}", searched);
            return data;
        } finally {
            this.releaseCollection(client, collectionName);
        }
    }

    private SearchResultData search(
            final MilvusClient client,
            final List<Double> queryVector,
            final String collectionName,
            final Integer topk) {
        try {
            this.loadCollection(client, collectionName);
            var floatVector = EmbeddingConverter.normalizeVector(queryVector);
            var searched = client.search(SearchParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withMetricType(MetricType.COSINE)
                    .withVectorFieldName(MilvusConstant.VECTOR_FIELD)
                    .withTopK(topk)
                    .withOutFields(List.of(MilvusConstant.COLLECTION_FIELD_NAME))
                    .withVectors(Collections.singletonList(floatVector))
                    .build());

            if (searched.getStatus() != R.Status.Success.getCode()) {
                log.error("Search failed: {}", searched);

                throw new RuntimeException(searched.getException());
            }

            var data = searched.getData().getResults();
            log.debug("Search finished: {}", searched);
            return data;
        } finally {
            this.releaseCollection(client, collectionName);
        }
    }

    private void loadCollection(final MilvusClient client, final String collectionName) {
        var loadedCollection = client.loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());

        if (loadedCollection.getStatus() != R.Status.Success.getCode()) {
            log.error("Collection load failed: {}", loadedCollection);

            throw new RuntimeException(loadedCollection.getException());
        }

        log.debug("Collection load: {}", loadedCollection);
    }

    private void releaseCollection(final MilvusClient client, final String collectionName) {
        var releasedCollection = client.releaseCollection(ReleaseCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());

        if (releasedCollection.getStatus() != R.Status.Success.getCode()) {
            log.error("Collection release failed: {}", releasedCollection);

            throw new RuntimeException(releasedCollection.getException());
        }

        log.debug("Collection released: {}", releasedCollection);
    }

    private void createIndex(final MilvusClient client, final String collectionName) {
        var createdIndex = client.createIndex(CreateIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withIndexName(MilvusConstant.INDEX_NAME + collectionName)
                .withIndexType(IndexType.FLAT)
                .withFieldName(MilvusConstant.VECTOR_FIELD)
                .withMetricType(MetricType.COSINE)
                .build());

        if (!createdIndex.getStatus().equals(R.Status.Success.getCode())) {
            log.error("Creating index failed: {}", createdIndex);

            throw new RuntimeException(createdIndex.getException());
        }

        log.debug("Created index: {}", createdIndex);
    }

    private long insertVector(final MilvusClient client, final String collectionName, final List<Double> embedding) {
        var floatVector = EmbeddingConverter.normalizeVector(embedding);
        var insertedVector = client.insert(InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(Arrays.asList(
                        new InsertParam.Field(
                                MilvusConstant.COLLECTION_FIELD_NAME, Collections.singletonList(collectionName)),
                        new InsertParam.Field(
                                MilvusConstant.VECTOR_FIELD, Collections.singletonList(floatVector)) // Vector field
                        )) // ID field as Long
                .build());

        if (!insertedVector.getStatus().equals(R.Status.Success.getCode())) {
            log.error("Inserting vector failed: {}", insertedVector);

            throw new RuntimeException(insertedVector.getException());
        }

        log.debug("Inserted vector: {}", insertedVector.getData().getIDs().getIntId());

        return insertedVector.getData().getIDs().getIntId().getData(0);
    }

    private void flush(final MilvusClient client, final String collectionName) {
        var flushed = client.flush(
                FlushParam.newBuilder().addCollectionName(collectionName).build());

        if (!flushed.getStatus().equals(R.Status.Success.getCode())) {
            log.error("Flushing failed: {}", flushed);

            throw new RuntimeException(flushed.getException());
        }

        log.debug("Flushed successfully: {}", flushed);
    }
}
