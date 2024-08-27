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

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.core.DefaultStreamingRag;
import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.StreamingGenerator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.retriever.Retriever;

/**
 * Autoconfiguration class for setting up a {@link DefaultStreamingRag} bean. This class provides the automatic
 * configuration for a {@link DefaultStreamingRag} instance, which is a key component in the Retrieval-Augmented
 * Generation (RAG) framework. The {@link DefaultStreamingRag} is configured with a list of Retrievers that fetch
 * relevant Context, Augmenters that enhance the Context, and a Generator that produces the final Response based on the
 * augmented Context.
 *
 * @author Nikita Litvinov
 * @see DefaultStreamingRag
 * @see Retriever
 * @see Augmenter
 * @see StreamingGenerator
 * @since 0.1.0
 */
@AutoConfiguration
public class StreamingRagAutoConfiguration {

    /**
     * Default constructor for {@link StreamingRagAutoConfiguration}.
     */
    public StreamingRagAutoConfiguration() {}

    /**
     * Defines a {@link DefaultStreamingRag} bean, which is configured with the provided lists of {@link Retriever} and
     * {@link Augmenter}, along with the {@link StreamingGenerator} used to generate Responses.
     *
     * @param retrievers the list of {@link Retriever} beans used for retrieving Context based on the Request.
     * @param augmenters the list of {@link Augmenter} beans used for enhancing the retrieved Context.
     * @param generator  the {@link StreamingGenerator} bean used for generating Responses in streaming manner based on
     *                   the augmented Context.
     * @return a configured {@link DefaultStreamingRag} instance.
     */
    @Bean
    public DefaultStreamingRag defaultStreamingRag(
            final List<Retriever<RagRequest, RagContext>> retrievers,
            final List<Augmenter<RagRequest, RagContext>> augmenters,
            final StreamingGenerator<RagRequest, RagResponse> generator) {
        return new DefaultStreamingRag(retrievers, augmenters, generator);
    }
}
