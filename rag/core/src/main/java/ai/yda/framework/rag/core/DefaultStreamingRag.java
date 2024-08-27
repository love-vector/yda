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

import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.StreamingGenerator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.retriever.Retriever;

import lombok.AccessLevel;
import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ai.yda.framework.rag.core.util.ContentUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides a default mechanism for executing a Retrieval-Augmented Generation (RAG) process in a streaming manner.
 * <p>
 * This class is useful when Responses need to be generated progressively, such as when dealing with large amounts of
 * data or when the Response is expected to be produced in chunks. The streaming rag process is executed by retrieving
 * relevant Contexts using a list of Retrievers, augmenting these Contexts using a list of Augmenters, and generating a
 * Response using a Generator in a streaming manner.
 * </p>
 *
 * @author Nikita Litvinov
 * @see Retriever
 * @see Augmenter
 * @see StreamingGenerator
 * @since 0.1.0
 */
@Getter(AccessLevel.PROTECTED)
public class DefaultStreamingRag implements StreamingRag<RagRequest, RagResponse> {

    private final List<Retriever<RagRequest, RagContext>> retrievers;

    private final List<Augmenter<RagRequest, RagContext>> augmenters;

    private final StreamingGenerator<RagRequest, RagResponse> generator;

    /**
     * Constructs a new {@link DefaultStreamingRag} instance with the specified retrievers, augmenters and generator.
     *
     * @param retrievers the list of {@link Retriever} objects used to retrieve {@link RagContext} data based on the
     *                   {@link RagRequest}.
     * @param augmenters the list of {@link Augmenter} objects used to augment or modify the list {@link RagContext}
     *                   based on the {@link RagRequest}.
     * @param generator  the {@link StreamingGenerator} object that generates the {@link Flux stream} of the of
     *                   {@link RagResponse} objects based on the {@link RagRequest}.
     */
    public DefaultStreamingRag(
            final List<Retriever<RagRequest, RagContext>> retrievers,
            final List<Augmenter<RagRequest, RagContext>> augmenters,
            final StreamingGenerator<RagRequest, RagResponse> generator) {
        this.retrievers = retrievers;
        this.augmenters = augmenters;
        this.generator = generator;
    }

    /**
     * Executes the Retrieval-Augmented Generation (RAG) process by retrieving relevant Contexts using a list of
     * Retrievers, augmenting these Contexts using a list of Augmenters, and generating a Response using a streaming
     * Generator.
     *
     * @param request the {@link RagRequest} object from the User.
     * @return a {@link Flux stream} of {@link RagResponse} objects containing the results of the RAG operation.
     */
    @Override
    public Flux<RagResponse> streamRag(final RagRequest request) {
        return Flux.fromStream(retrievers.parallelStream())
                .flatMap(retriever -> Mono.fromCallable(() -> retriever.retrieve(request)))
                .collectList()
                .flatMap(contexts -> {
                    for (var augmenter : augmenters) {
                        contexts = augmenter.augment(request, contexts);
                    }
                    return Mono.just(contexts);
                })
                .flatMap(this::mergeContexts)
                .flatMapMany(context -> generator.streamGeneration(request, context));
    }

    /**
     * Merges the knowledge into a single string. Each piece of knowledge is separated by a point character.
     *
     * @param contexts the list of {@link RagContext} objects containing knowledge data. Each piece of knowledge is
     *                 separated by a point character.
     * @return a {@link Mono<String>} that emits a single string, which is the result of combining all pieces of
     * knowledge from the provided contexts.
     */
    protected Mono<String> mergeContexts(final List<RagContext> contexts) {
        return Flux.fromStream(contexts.parallelStream())
                .map(ragContext -> String.join(ContentUtil.SENTENCE_SEPARATOR, ragContext.getKnowledge()))
                .collect(Collectors.joining(ContentUtil.SENTENCE_SEPARATOR));
    }
}
