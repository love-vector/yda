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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.generator.StreamingGenerator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.model.RequestTransformer;
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
 * @since 0.1.0
 */
@Getter(AccessLevel.PROTECTED)
public class DefaultRag implements Rag<RagRequest, RagResponse>, StreamingRag<RagRequest, RagResponse> {

    private final List<Retriever<RagRequest, RagContext>> retrievers;

    private final List<Augmenter<RagRequest, RagContext>> augmenters;

    private final Generator<RagRequest, RagResponse> generator;

    private final StreamingGenerator<RagRequest, RagResponse> streamingGenerator;

    private final List<RequestTransformer<RagRequest>> requestTransformers;

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
            final Generator<RagRequest, RagResponse> generator,
            final StreamingGenerator<RagRequest, RagResponse> streamingGenerator,
            final List<RequestTransformer<RagRequest>> requestTransformers) {
        if (generator == null && streamingGenerator == null) {
            throw new IllegalArgumentException("At least one of Generator or StreamingGenerator must be provided.");
        }
        this.retrievers = retrievers;
        this.augmenters = augmenters;
        this.generator = generator;
        this.streamingGenerator = streamingGenerator;
        this.requestTransformers = requestTransformers;
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
        if (generator == null) {
            throw new IllegalStateException("Generator is required to use this method.");
        }
        var transformingRequest = request;
        for (RequestTransformer<RagRequest> requestTransformer : requestTransformers) {
            transformingRequest = requestTransformer.transformRequest(transformingRequest);
        }
        var transformedRequest = transformingRequest;
        var contexts = retrievers.parallelStream()
                .map(retriever -> retriever.retrieve(transformedRequest))
                .collect(Collectors.toUnmodifiableList());
        for (var augmenter : augmenters) {
            contexts = augmenter.augment(transformedRequest, contexts);
        }
        return generator.generate(transformedRequest, mergeContexts(contexts));
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
        if (streamingGenerator == null) {
            throw new IllegalStateException("StreamingGenerator is required to use this method.");
        }
        return Flux.just(requestTransformers)
                .flatMap(transformers -> {
                    var transformingRequest = request;
                    for (var requestTransformer : transformers) {
                        transformingRequest = requestTransformer.transformRequest(transformingRequest);
                    }
                    return Mono.just(transformingRequest);
                })
                .flatMap(transformedRequest -> Flux.fromStream(retrievers.parallelStream())
                        .flatMap(retriever -> Mono.fromCallable(() -> retriever.retrieve(transformedRequest)))
                        .collectList()
                        .flatMap(contexts -> {
                            for (var augmenter : augmenters) {
                                contexts = augmenter.augment(transformedRequest, contexts);
                            }
                            return Mono.just(contexts);
                        })
                        .flatMap(this::mergeReactiveContexts)
                        .flatMapMany(context -> streamingGenerator.streamGeneration(transformedRequest, context)));
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

    /**
     * Merges the knowledge into a single string. Each piece of knowledge is separated by a point character.
     *
     * @param contexts the list of {@link RagContext} objects containing knowledge data. Each piece of knowledge is
     *                 separated by a point character.
     * @return a {@link Mono<String>} that emits a single string, which is the result of combining all pieces of
     * knowledge from the provided contexts.
     */
    protected Mono<String> mergeReactiveContexts(final List<RagContext> contexts) {
        return Flux.fromStream(contexts.parallelStream())
                .map(ragContext -> String.join(ContentUtil.SENTENCE_SEPARATOR, ragContext.getKnowledge()))
                .collect(Collectors.joining(ContentUtil.SENTENCE_SEPARATOR));
    }
}
