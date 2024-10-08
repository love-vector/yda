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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.StreamingGenerator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.util.ContentUtil;
import ai.yda.framework.rag.core.util.StreamingRequestTransformer;

/**
 * Default implementation of the Retrieval-Augmented Generation (RAG) process in a streaming manner.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
public class DefaultStreamingRag implements StreamingRag<RagRequest, RagResponse> {

    /**
     * The list of {@link Retriever} instances used to retrieving {@link RagContext} based on the {@link RagRequest}.
     */
    private final List<Retriever<RagRequest, RagContext>> retrievers;

    /**
     * The list of {@link Augmenter} instances used to modify or enhance the retrieved {@link RagContext}.
     */
    private final List<Augmenter<RagRequest, RagContext>> augmenters;

    /**
     * The {@link StreamingGenerator} responsible for generating the final {@link RagResponse} in a streaming manner.
     */
    private final StreamingGenerator<RagRequest, RagResponse> streamingGenerator;

    /**
     * The list of {@link StreamingRequestTransformer} instances used to transform the incoming {@link RagRequest}.
     */
    private final List<StreamingRequestTransformer<RagRequest>> requestTransformers;

    /**
     * Constructs a new {@link DefaultStreamingRag} instance.
     *
     * @param retrievers          the list of {@link Retriever} objects used to retrieve {@link RagContext} data.
     * @param augmenters          the list of {@link Augmenter} objects used to augment the retrieved Contexts.
     * @param streamingGenerator  the {@link StreamingGenerator} used to generate {@link RagResponse} objects in a
     *                            streaming manner.
     * @param requestTransformers the list of {@link StreamingRequestTransformer} objects used to transform the
     *                            {@link RagRequest}.
     */
    public DefaultStreamingRag(
            final List<Retriever<RagRequest, RagContext>> retrievers,
            final List<Augmenter<RagRequest, RagContext>> augmenters,
            final StreamingGenerator<RagRequest, RagResponse> streamingGenerator,
            final List<StreamingRequestTransformer<RagRequest>> requestTransformers) {
        this.retrievers = retrievers;
        this.augmenters = augmenters;
        this.streamingGenerator = streamingGenerator;
        this.requestTransformers = requestTransformers;
    }

    /**
     * Executes the Retrieval-Augmented Generation (RAG) process in a streaming manner by:
     * <ul>
     *     <li>
     *         Transforming the initial {@link RagRequest} using the provided {@link StreamingRequestTransformer}
     *         instances.
     *     </li>
     *     <li>Retrieving relevant {@link RagContext} from the {@link Retriever} instances.</li>
     *     <li>Augmenting the retrieved Contexts using the provided {@link Augmenter} instances.</li>
     *     <li>Generating a stream of {@link RagResponse} objects using the {@link StreamingGenerator}.</li>
     * </ul>
     *
     * @param request the {@link RagRequest} to process.
     * @return a {@link Flux} stream of generated {@link RagResponse} objects.
     */
    @Override
    public Flux<RagResponse> streamRag(final RagRequest request) {
        return Flux.fromIterable(requestTransformers)
                .reduce(
                        Mono.just(request),
                        (monoRequest, transformer) -> monoRequest.flatMap(transformer::transformRequest))
                .flatMap(monoRequest -> monoRequest)
                .flatMapMany(transformedRequest -> Flux.fromIterable(retrievers)
                        .flatMap(retriever -> Mono.fromCallable(() -> retriever.retrieve(transformedRequest))
                                .subscribeOn(Schedulers.boundedElastic()))
                        .collectList()
                        .flatMap(contexts -> {
                            for (var augmenter : augmenters) {
                                contexts = augmenter.augment(transformedRequest, contexts);
                            }
                            return Mono.just(contexts);
                        })
                        .flatMap(this::mergeContexts)
                        .flatMapMany(context -> streamingGenerator.streamGeneration(transformedRequest, context)));
    }

    /**
     * Merges the knowledge into a single string. Each piece of knowledge is separated by a point character.
     *
     * @param contexts the list of {@link RagContext} objects containing knowledge data.
     * @return a {@link Mono<String>} that emits a single string combining all pieces of knowledge from the provided
     * contexts.
     */
    protected Mono<String> mergeContexts(final List<RagContext> contexts) {
        return Flux.fromStream(contexts.parallelStream())
                .map(ragContext -> String.join(ContentUtil.SENTENCE_SEPARATOR, ragContext.getKnowledge()))
                .collect(Collectors.joining(ContentUtil.SENTENCE_SEPARATOR));
    }
}
