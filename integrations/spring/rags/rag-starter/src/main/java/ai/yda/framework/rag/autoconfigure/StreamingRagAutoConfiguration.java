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
package ai.yda.framework.rag.autoconfigure;

import java.util.List;

import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.core.BaseStreamingRag;
import ai.yda.framework.rag.core.generator.StreamingGenerator;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.retriever.BaseRetriever;

/**
 * Autoconfiguration class for setting up a {@link BaseStreamingRag} bean in the RAG framework.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class StreamingRagAutoConfiguration extends AbstractRagAutoConfiguration {

    /**
     * Default constructor for {@link StreamingRagAutoConfiguration}.
     */
    public StreamingRagAutoConfiguration() {}

    /**
     * Creates and configures a {@link BaseStreamingRag} bean.
     *
     * @param retrievers         the list of {@link DocumentRetriever} beans for retrieving Context based on the
     *                           Request.
     * @param augmenters         the list of {@link QueryAugmenter} beans for enhancing the retrieved Context.
     * @param streamingGenerator the {@link StreamingGenerator} bean for generating Responses in a streaming
     * @return a configured {@link BaseStreamingRag} instance.
     */
    @Bean
    public BaseStreamingRag defaultStreamingRag(
            final List<BaseRetriever> retrievers,
            final List<QueryAugmenter> augmenters,
            final StreamingGenerator<Query, RagResponse> streamingGenerator) {
        return new BaseStreamingRag(retrievers, augmenters, streamingGenerator);
    }
}
