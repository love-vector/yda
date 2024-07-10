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

import java.util.Map;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.retriever.filesystem.FilesystemRetriever;
import ai.yda.framework.rag.retriever.filesystem.config.FilesystemRetrieverConfig;
import ai.yda.framework.rag.retriever.filesystem.factory.FilesystemRetrieverFactory;

@AutoConfiguration
@EnableConfigurationProperties({RetrieverFilesystemProperties.class})
public class RetrieverFilesystemAutoConfiguration {

    @Bean
    public FilesystemRetriever filesystemRetriever(
            FilesystemRetrieverFactory filesystemRetrieverFactory, RetrieverFilesystemProperties properties) {
        return filesystemRetrieverFactory.createRetriever(
                Map.of(FilesystemRetrieverConfig.LOCAL_DIRECTORY_PATH, properties.getLocalDirectoryPath()));
    }

    @Bean
    public FilesystemRetrieverFactory retrieverFactory(RetrieverFilesystemProperties properties) {
        var milvusClient = milvusClient(properties);
        var embeddingModel = embeddingModel(properties);
        var vectorStore = vectorStore(milvusClient, embeddingModel, properties);
        return new FilesystemRetrieverFactory(vectorStore);
    }

    private VectorStore vectorStore(
            MilvusServiceClient milvusClient, EmbeddingModel embeddingModel, RetrieverFilesystemProperties properties) {
        var config = MilvusVectorStore.MilvusVectorStoreConfig.builder()
                .withCollectionName(properties.getCollectionName())
                .withDatabaseName(properties.getDatabaseName())
                .withIndexType(IndexType.IVF_FLAT)
                .withMetricType(MetricType.COSINE)
                .withEmbeddingDimension(properties.getEmbeddingDimension())
                .build();
        return new MilvusVectorStore(milvusClient, embeddingModel, config, Boolean.TRUE);
    }

    private EmbeddingModel embeddingModel(RetrieverFilesystemProperties properties) {
        var openAiApi = new OpenAiApi(properties.getOpenAiKey());
        return new OpenAiEmbeddingModel(
                openAiApi,
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .withModel(properties.getOpenAiModel())
                        .withUser("user")
                        .build(),
                RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    private MilvusServiceClient milvusClient(RetrieverFilesystemProperties properties) {
        return new MilvusServiceClient(ConnectParam.newBuilder()
                .withAuthorization(properties.getUsername(), properties.getPassword())
                .withUri(properties.getHost())
                .build());
    }
}
