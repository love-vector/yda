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
import ai.yda.framework.rag.core.util.StringUtil;

/**
 * Provides a default mechanism for executing a Retrieval-Augmented Generation (RAG) process.
 * <p>
 * The RAG process is executed by retrieving relevant contexts using a list of retrievers, augmenting these contexts
 * using a list of augmenters, and generating a response using a generator. First, it uses a parallel stream to retrieve
 * relevant contexts from the input {@link RagRequest} using a list of {@link Retriever} instances. Each retriever
 * processes the request independently, and the retrieved contexts are collected into an unmodifiable list. Next, the
 * method iterates through the list of {@link Augmenter} instances, augmenting the collected contexts. Each augmenter
 * modifies the list of contexts based on the input request and previously retrieved contexts. Finally, the method calls
 * the {@link Generator#generate(RagRequest, String)} method, passing the original request and the merged contexts
 * (combined into a single string) as arguments. This generates the final {@link RagResponse} which is returned as the
 * result of the RAG operation.
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
     * Constructs a new {@link DefaultRag} instance with the specified retrievers, augmenters and generator.
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
     * {@inheritDoc}
     * Executes the Retrieval-Augmented Generation (RAG) process by sequentially retrieving, augmenting, and generating
     * the final response.
     *
     * @param request the {@link RagRequest} object from the user.
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
     * Merges the knowledge from the list of {@link RagContext} objects into a single string. Each piece of knowledge is
     * separated by a point character.
     *
     * @param contexts the list of {@link RagContext} objects containing knowledge data.
     * @return a string that combines all pieces of knowledge from the contexts.
     */
    protected String mergeContexts(final List<RagContext> contexts) {
        return contexts.parallelStream()
                .map(ragContext -> String.join(StringUtil.POINT, ragContext.getKnowledge()))
                .collect(Collectors.joining(StringUtil.POINT));
    }
}
