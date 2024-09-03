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
package ai.yda.framework.rag.core;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;

import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.util.ContentUtil;

/**
 * Provides a default mechanism for executing a Retrieval-Augmented Generation (RAG) process.
 * <p>
 * The RAG process is executed by retrieving relevant Contexts using a list of Retrievers, augmenting these Contexts
 * using a list of Augmenters, and generating a Response using a Generator.
 * </p>
 *
 * @author Nikita Litvinov
 * @see Retriever
 * @see Augmenter
 * @see Generator
 * @see DefaultStreamingRag
 * @since 0.1.0
 */
@Getter(AccessLevel.PROTECTED)
public class DefaultRag implements Rag<RagRequest, RagResponse> {

    private final List<Retriever<RagRequest, RagContext>> retrievers;

    private final List<Augmenter<RagRequest, RagContext>> augmenters;

    private final Generator<RagRequest, RagResponse> generator;

    /**
     * Constructs a new {@link DefaultRag} instance with the specified Retrievers, Augmenters and Generator.
     *
     * @param retrievers the list of {@link Retriever} objects used to retrieve {@link RagContext} data based on the
     *                   {@link RagRequest}.
     * @param augmenters the list of {@link Augmenter} objects used to augment or modify the list {@link RagContext}
     *                   based on the {@link RagRequest}.
     * @param generator  the {@link Generator} object that generates the {@link RagResponse} based on the
     *                   {@link RagRequest}.
     */
    public DefaultRag(
            final List<Retriever<RagRequest, RagContext>> retrievers,
            final List<Augmenter<RagRequest, RagContext>> augmenters,
            final Generator<RagRequest, RagResponse> generator) {
        this.retrievers = retrievers;
        this.augmenters = augmenters;
        this.generator = generator;
    }

    /**
     * Executes the Retrieval-Augmented Generation (RAG) process by retrieving relevant Contexts using a list of
     * Retrievers, augmenting these Contexts using a list of Augmenters, and generating a Response using a Generator.
     *
     * @param request the {@link RagRequest} object from the User.
     * @return the {@link RagResponse} object containing the results of the RAG operation.
     */
    @Override
    public RagResponse doRag(final RagRequest request) {
        var contexts = retrievers.parallelStream()
                .map(retriever -> retriever.retrieve(request))
                .collect(Collectors.toUnmodifiableList());
        for (var augmenter : augmenters) {
            contexts = augmenter.augment(request, contexts);
        }
        return generator.generate(request, mergeContexts(contexts));
    }

    /**
     * Merges the Knowledge from the list of {@link RagContext} objects into a single string. Each piece of Knowledge is
     * separated by a point character.
     *
     * @param contexts the list of {@link RagContext} objects containing Knowledge data.
     * @return a string that combines all pieces of Knowledge from the Contexts.
     */
    protected String mergeContexts(final List<RagContext> contexts) {
        return contexts.parallelStream()
                .map(ragContext -> String.join(ContentUtil.SENTENCE_SEPARATOR, ragContext.getKnowledge()))
                .collect(Collectors.joining(ContentUtil.SENTENCE_SEPARATOR));
    }
}
