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
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.core.DefaultRag;
import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.util.RequestTransformer;

/**
 * Autoconfiguration class for setting up a {@link DefaultRag} bean in the RAG framework.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class RagAutoConfiguration {

    /**
     * Default constructor for {@link RagAutoConfiguration}.
     */
    public RagAutoConfiguration() {}

    /**
     * Creates and configures a {@link DefaultRag} bean.
     *
     * @param retrievers          the list of {@link Retriever} beans for retrieving Context based on the Request.
     * @param augmenters          the list of {@link Augmenter} beans for enhancing the retrieved Context.
     * @param generator           the {@link Generator} bean for generating Responses based on the augmented Context.
     * @param requestTransformers the list of {@link RequestTransformer} beans for transforming Requests before
     *                            processing.
     * @return a configured {@link DefaultRag} instance.
     */
    @Bean
    public DefaultRag defaultRag(
            final List<Retriever<RagRequest, RagContext>> retrievers,
            final List<Augmenter<RagRequest, RagContext>> augmenters,
            final Generator<RagRequest, RagResponse> generator,
            final List<RequestTransformer<RagRequest>> requestTransformers) {
        return new DefaultRag(retrievers, augmenters, generator, requestTransformers);
    }
}
