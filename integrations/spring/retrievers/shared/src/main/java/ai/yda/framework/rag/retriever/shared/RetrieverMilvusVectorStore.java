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

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.collection.DropCollectionParam;
import io.milvus.param.collection.HasCollectionParam;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;

/**
 * An extended implementation of {@link MilvusVectorStore} that provides additional functionality
 * for managing Milvus collections dynamically, including clearing or dropping collections
 * during startup based on configuration.
 *
 * <p>This class is specifically designed for use cases where retrievers require fine-grained
 * control over Milvus collections, such as removing stale data or resetting the collection
 * state upon initialization.</p>
 *
 * @author Iryna Kopchak
 * @see MilvusVectorStore
 * @see MilvusServiceClient
 * @since 0.1.0
 */
public class RetrieverMilvusVectorStore extends MilvusVectorStore {

    private final MilvusServiceClient milvusClient;
    private final String databaseName;
    private final String collectionName;
    private final boolean dropCollectionOnStartup;

    /**
     * Constructs a new {@link  RetrieverMilvusVectorStore} instance with the specified parameters.
     *
     * @param milvusClient             the client for interacting with the Milvus service.
     * @param embeddingModel           the model used for embedding document content.
     * @param initializeSchema         whether to initialize the schema in the database.
     * @param collectionName           the name of the collection in the Milvus database.
     * @param databaseName             the name of the database in Milvus.
     * @param dropCollectionOnStartup  whether to drop the collection entirely on startup.
     */
    public RetrieverMilvusVectorStore(
            final MilvusServiceClient milvusClient,
            final EmbeddingModel embeddingModel,
            final boolean initializeSchema,
            final String collectionName,
            final String databaseName,
            final boolean dropCollectionOnStartup) {

        super(MilvusVectorStore.builder(milvusClient, embeddingModel)
                .initializeSchema(initializeSchema)
                .collectionName(collectionName)
                .databaseName(databaseName));
        this.milvusClient = milvusClient;
        this.collectionName = collectionName;
        this.databaseName = databaseName;
        this.dropCollectionOnStartup = dropCollectionOnStartup;
    }

    /**
     * Initializes the vector store after all properties have been set.
     *
     * <p>If {@code dropCollectionOnStartup} is enabled, the collection will be dropped
     * during initialization. This method also ensures the parent class lifecycle method is invoked.</p>
     *
     * @throws Exception if an error occurs during initialization.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.dropCollectionOnStartup) {
            dropCollection();
        }
        super.afterPropertiesSet();
    }

    /**
     * Drops the specified Milvus collection if it exists in the database.
     *
     * @throws RuntimeException if the operation fails.
     */
    private void dropCollection() {
        if (isDatabaseCollectionExists()) {
            this.milvusClient.dropCollection(DropCollectionParam.newBuilder()
                    .withDatabaseName(databaseName)
                    .withCollectionName(collectionName)
                    .build());
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
}
