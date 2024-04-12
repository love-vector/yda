package org.vector.assistant.persistance.dao;

import java.util.List;

import io.milvus.client.MilvusServiceClient;
import io.milvus.exception.MilvusException;
import io.milvus.grpc.DataType;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.*;
import io.milvus.param.index.CreateIndexParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.stereotype.Component;

/**
 * Provides data access operations for managing collections in a Milvus database.
 * This class includes methods to create and delete collections, ensuring operations
 * are validated for success.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MilvusDao {

    private final MilvusServiceClient milvusServiceClient;

    /**
     * Creates a collection if it does not already exist. This method sets up the collection
     * with predefined fields and an index on the 'embedding' field using IVF_FLAT indexing
     * and cosine similarity.
     *
     * @param collectionName the name of the collection to be created.
     * @throws MilvusException if the operation fails, encapsulating error details and status.
     */
    public void createCollectionIfNotExist(final String collectionName) {
        var createCollection = milvusServiceClient.createCollection(CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldTypes(List.of(
                        DEFAULT_COLLECTION_FIELDS.ID,
                        DEFAULT_COLLECTION_FIELDS.CONTENT,
                        DEFAULT_COLLECTION_FIELDS.METADATA,
                        DEFAULT_COLLECTION_FIELDS.EMBEDDING))
                .build());
        validateResult(createCollection);

        createIndex(collectionName);
    }

    /**
     * Deletes a specified collection.
     *
     * @param collectionName the name of the collection to be deleted.
     * @throws MilvusException if the operation fails, encapsulating error details and status.
     */
    public void deleteCollectionByName(final String collectionName) {
        var dropCollection = milvusServiceClient.dropCollection(DropCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
        validateResult(dropCollection);
    }

    private void createIndex(final String collectionName) {
        var createdIndex = milvusServiceClient.createIndex(CreateIndexParam.newBuilder()
                .withDatabaseName(MilvusVectorStore.DEFAULT_DATABASE_NAME)
                .withCollectionName(collectionName)
                .withIndexName(DEFAULT_COLLECTION_FIELDS.EMBEDDING.getName())
                .withIndexType(IndexType.IVF_FLAT)
                .withFieldName(DEFAULT_COLLECTION_FIELDS.EMBEDDING.getName())
                .withMetricType(MetricType.COSINE)
                .withExtraParam("{\"nlist\":1024}")
                .build());
        validateResult(createdIndex);
    }

    private <T> void validateResult(final R<T> result) {
        if (!result.getStatus().equals(R.Status.Success.getCode())) {
            log.error("Something went wrong during the Milvus query: {}", result.getMessage());
            throw new MilvusException(result.getMessage(), result.getStatus());
        }
    }

    /**
     * Defines the default field types for collections in Milvus. This class provides a set
     * of predefined field types that are commonly used in collections, such as ID, content,
     * metadata, and embedding vectors.
     */
    static final class DEFAULT_COLLECTION_FIELDS {
        // A primary key field for the collection, typically holding a unique identifier.
        public static final FieldType ID = FieldType.newBuilder()
                .withName("doc_id")
                .withPrimaryKey(Boolean.TRUE)
                .withDataType(DataType.VarChar)
                .withMaxLength(36)
                .build();

        // A field to store textual content, allowing a large amount of text.
        public static final FieldType CONTENT = FieldType.newBuilder()
                .withName("content")
                .withDataType(DataType.VarChar)
                .withMaxLength(65535)
                .build();

        // A field to store JSON formatted metadata related to the content.
        public static final FieldType METADATA = FieldType.newBuilder()
                .withName("metadata")
                .withDataType(DataType.JSON)
                .withMaxLength(500)
                .build();

        // A floating-point vector field used for storing embedding vectors, which
        // are typically used in similarity searches and machine learning applications.
        public static final FieldType EMBEDDING = FieldType.newBuilder()
                .withName("embedding")
                .withDataType(DataType.FloatVector)
                .withDimension(MilvusVectorStore.OPENAI_EMBEDDING_DIMENSION_SIZE)
                .build();

        private DEFAULT_COLLECTION_FIELDS() {}
    }
}
