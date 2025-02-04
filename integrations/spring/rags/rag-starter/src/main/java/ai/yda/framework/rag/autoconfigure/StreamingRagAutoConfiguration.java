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

import org.springframework.ai.document.Document;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.core.DefaultStreamingRag;
import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.StreamingGenerator;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.util.StreamingRequestTransformer;

/**
 * Autoconfiguration class for setting up a {@link DefaultStreamingRag} bean in the RAG framework.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class StreamingRagAutoConfiguration {

    /**
     * Default constructor for {@link StreamingRagAutoConfiguration}.
     */
    public StreamingRagAutoConfiguration() {}

    /**
     * Creates and configures a {@link DefaultStreamingRag} bean.
     *
     * @param retrievers                   the list of {@link Retriever} beans for retrieving Context based on the
     *                                     Request.
     * @param augmenters                   the list of {@link Augmenter} beans for enhancing the retrieved Context.
     * @param streamingGenerator           the {@link StreamingGenerator} bean for generating Responses in a streaming
     *                                     manner.
     * @param streamingRequestTransformers the list of {@link StreamingRequestTransformer} beans for transforming
     *                                     Requests before processing.
     * @return a configured {@link DefaultStreamingRag} instance.
     */
    @Bean
    public DefaultStreamingRag defaultStreamingRag(
            final List<Retriever<RagRequest, Document>> retrievers,
            final List<Augmenter<RagRequest, Document>> augmenters,
            final StreamingGenerator<RagRequest, RagResponse> streamingGenerator,
            final List<StreamingRequestTransformer<RagRequest>> streamingRequestTransformers) {
        return new DefaultStreamingRag(retrievers, augmenters, streamingGenerator, streamingRequestTransformers);
    }
}
