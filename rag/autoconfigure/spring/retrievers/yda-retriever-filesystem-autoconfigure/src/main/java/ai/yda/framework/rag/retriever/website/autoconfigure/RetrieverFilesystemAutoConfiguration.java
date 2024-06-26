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
package ai.yda.framework.rag.retriever.website.autoconfigure;

import java.util.HashMap;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.rag.core.model.impl.BaseRagContext;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.retriever.factory.RetrieverFactory;
import ai.yda.framework.rag.retriever.filesystem.factory.FilesystemRetrieverFactory;

import static ai.yda.framework.rag.retriever.filesystem.config.FilesystemRetrieverConfig.LOCAL_DIRECTORY_PATH;

@AutoConfiguration
@EnableConfigurationProperties({RetrieverFilesystemProperties.class})
public class RetrieverFilesystemAutoConfiguration {

    @Bean
    public Retriever<BaseAssistantRequest, BaseRagContext> filesystemRetriever(
            RetrieverFactory<BaseAssistantRequest, BaseRagContext> retrieverFactory,
            RetrieverFilesystemProperties properties) {

        return retrieverFactory.createRetriever(new HashMap<>() {
            {
                put(LOCAL_DIRECTORY_PATH, properties.getLocalDirectoryPath());
            }
        });
    }

    @Bean
    public RetrieverFactory<BaseAssistantRequest, BaseRagContext> retrieverFactory(VectorStore vectorStore) {
        return new FilesystemRetrieverFactory(vectorStore);
    }
}
