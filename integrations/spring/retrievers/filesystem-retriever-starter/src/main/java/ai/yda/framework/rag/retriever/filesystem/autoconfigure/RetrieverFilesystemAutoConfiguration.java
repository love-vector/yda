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

import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusServiceClientProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.retriever.filesystem.FilesystemRetriever;
import ai.yda.framework.rag.retriever.shared.MilvusVectorStoreUtil;

@AutoConfiguration
@EnableConfigurationProperties(RetrieverFilesystemProperties.class)
public class RetrieverFilesystemAutoConfiguration {

    @Bean
    public FilesystemRetriever filesystemRetriever(
            final RetrieverFilesystemProperties filesystemProperties,
            final MilvusVectorStoreProperties milvusProperties,
            final MilvusServiceClientProperties milvusClientProperties,
            final OpenAiConnectionProperties openAiConnectionProperties,
            final OpenAiChatProperties openAiChatProperties)
            throws Exception {

        var milvusVectorStore = MilvusVectorStoreUtil.createMilvusVectorStore(
                filesystemProperties,
                milvusProperties,
                milvusClientProperties,
                openAiConnectionProperties,
                openAiChatProperties);
        milvusVectorStore.afterPropertiesSet();
        return new FilesystemRetriever(
                milvusVectorStore,
                filesystemProperties.getFileStoragePath(),
                filesystemProperties.getTopK(),
                filesystemProperties.getIsProcessingEnabled());
    }
}
