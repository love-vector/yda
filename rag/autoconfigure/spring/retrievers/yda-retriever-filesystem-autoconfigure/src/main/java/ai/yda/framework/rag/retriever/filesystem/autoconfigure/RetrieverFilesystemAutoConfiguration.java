/*
 * Copyright 2023 - 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.yda.framework.rag.retriever.filesystem.autoconfigure;

import java.util.HashMap;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import ai.yda.framework.rag.retriever.filesystem.FilesystemRetriever;
import ai.yda.framework.rag.retriever.filesystem.factory.FilesystemRetrieverFactory;

import static ai.yda.framework.rag.retriever.filesystem.config.FilesystemRetrieverConfig.LOCAL_DIRECTORY_PATH;

@AutoConfiguration
@EnableConfigurationProperties({RetrieverFilesystemProperties.class})
public class RetrieverFilesystemAutoConfiguration {

    @Bean
    public FilesystemRetriever filesystemRetriever(
            FilesystemRetrieverFactory filesystemRetrieverFactory, RetrieverFilesystemProperties properties) {
        return filesystemRetrieverFactory.createRetriever(new HashMap<>() {
            {
                put(LOCAL_DIRECTORY_PATH, properties.getLocalDirectoryPath());
            }
        });
    }

    @Bean
    public FilesystemRetrieverFactory retrieverFactory(@Qualifier("filesystemVectorStore") VectorStore vectorStore) {
        return new FilesystemRetrieverFactory(vectorStore);
    }

    @Bean("filesystemVectorStore")
    @Primary
    public VectorStore vectorStore(
            MilvusServiceClient milvusClient, EmbeddingModel embeddingModel, RetrieverFilesystemProperties properties) {
        MilvusVectorStore.MilvusVectorStoreConfig config = MilvusVectorStore.MilvusVectorStoreConfig.builder()
                .withCollectionName(properties.getCollectionName())
                .withDatabaseName(properties.getDatabaseName())
                .withIndexType(IndexType.IVF_FLAT)
                .withMetricType(MetricType.COSINE)
                .withEmbeddingDimension(properties.getEmbeddingDimension())
                .build();
        return new MilvusVectorStore(milvusClient, embeddingModel, config, Boolean.TRUE);
    }
}
