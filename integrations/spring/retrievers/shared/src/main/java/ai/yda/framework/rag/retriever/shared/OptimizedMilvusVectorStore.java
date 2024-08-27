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

/**
 * The implementation of the {@link MilvusVectorStore} class that provides methods for optimized adding documents to the
 * vector storage and clearing collection on startup.
 *
 * @author Iryna Kopchak
 * @see EmbeddingModel
 * @see MilvusServiceClient
 * @see MilvusVectorStoreConfig
 * @since 0.1.0
 */
public class OptimizedMilvusVectorStore extends MilvusVectorStore {

    /**
     * The maximum number of dimensions for an embedding array. This is the limitation of the OpenAi API.
     */
    private static final int MAX_EMBEDDING_ARRAY_DIMENSIONS = 2048;

    private final MilvusServiceClient milvusClient;
    private final EmbeddingModel embeddingModel;
    private final String databaseName;
    private final String collectionName;
    private final boolean clearCollectionOnStartup;

    /**
     * Constructs a new {@link  OptimizedMilvusVectorStore} instance with the specified parameters.
     *
     * @param milvusClient             the Client for interacting with the Milvus Service.
     * @param embeddingModel           the Model used for Embedding document content.
     * @param config                   the configuration for the Milvus Vector Store.
     * @param initializeSchema         whether to initialize the schema in the database.
     * @param collectionName           the name of the collection in the Milvus database.
     * @param databaseName             the name of the database in Milvus.
     * @param clearCollectionOnStartup whether to clear the collection on startup.
     */
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

    /**
     * Adds a list of documents to the Milvus collection. The documents are embedded using the specified
     * {@link EmbeddingModel} before insertion.
     *
     * @param documents the list of documents to be added to the collection.
     * @throws RuntimeException if the insertion fails.
     */
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

    /**
     * Initializes the Vector Store after properties are set. Optionally clears the collection on startup if configured
     * to do so.
     *
     * @throws Exception if an error occurs during initialization.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.clearCollectionOnStartup) {
            clearCollection();
        }
        super.afterPropertiesSet();
    }

    /**
     * Embeds the content of documents using the {@link EmbeddingModel}.
     *
     * @param documentsContent the content of the documents to be embedded.
     * @return a list of embedded vectors.
     */
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

    /**
     * Clears the Milvus collection if it exists. This method deletes all entities from the collection.
     */
    private void clearCollection() {
        if (isDatabaseCollectionExists()) {
            var allEntitiesIds = getAllEntitiesIds();
            delete(allEntitiesIds);
        }
    }

    /**
     * Checks whether the specified collection exists in the Milvus database.
     *
     * @return {@code true} if the collection exists, {@code false} otherwise.
     * @throws RuntimeException if the collection existence check fails.
     */
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

    /**
     * Retrieves all entity IDs from the Milvus collection.
     *
     * @return a list of entity IDs.
     * @throws RuntimeException if the query fails.
     */
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
