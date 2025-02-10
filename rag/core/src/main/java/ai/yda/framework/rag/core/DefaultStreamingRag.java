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

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import ai.yda.framework.rag.core.generator.StreamingGenerator;
import ai.yda.framework.rag.core.model.RagResponse;

/**
 * Default implementation of the Retrieval-Augmented Generation (RAG) process in a streaming manner.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
public class DefaultStreamingRag implements StreamingRag<Query, RagResponse> {

    /**
     * The list of {@link DocumentRetriever} instances used to retrieving {@link Document}
     */
    private final List<DocumentRetriever> retrievers;

    /**
     * The list of {@link QueryAugmenter} instances used to modify or enhance the retrieved {@link Query}.
     */
    private final List<QueryAugmenter> augmenters;

    /**
     * The {@link StreamingGenerator} responsible for generating the final {@link RagResponse} in a streaming manner.
     */
    private final StreamingGenerator<Query, RagResponse> streamingGenerator;

    /**
     * Constructs a new {@link DefaultStreamingRag} instance.
     *
     * @param retrievers         the list of {@link DocumentRetriever} objects used to retrieve {@link Document} data.
     * @param augmenters         the list of {@link QueryAugmenter} objects used to augment the retrieved Contexts.
     * @param streamingGenerator the {@link StreamingGenerator} used to generate {@link RagResponse} objects in a
     *                           streaming manner.
     */
    public DefaultStreamingRag(
            final List<DocumentRetriever> retrievers,
            final List<QueryAugmenter> augmenters,
            final StreamingGenerator<Query, RagResponse> streamingGenerator) {
        this.retrievers = retrievers;
        this.augmenters = augmenters;
        this.streamingGenerator = streamingGenerator;
    }

    /**
     * Executes the Retrieval-Augmented Generation (RAG) process in a streaming manner by:
     * <ul>
     *     <li>Retrieving relevant {@link Document} from the {@link DocumentRetriever} instances.</li>
     *     <li>Augmenting the retrieved Contexts using the provided {@link QueryAugmenter} instances.</li>
     *     <li>Generating a stream of {@link RagResponse} objects using the {@link StreamingGenerator}.</li>
     * </ul>
     *
     * @param query the {@link Query} to process.
     * @return a {@link Flux} stream of generated {@link RagResponse} objects.
     */
    @Override
    public Flux<RagResponse> streamRag(final Query query) {
        return Flux.fromIterable(retrievers)
                .flatMap(retriever ->
                        Mono.fromCallable(() -> retriever.retrieve(query)).subscribeOn(Schedulers.boundedElastic()))
                .collectList()
                .map(lists -> lists.stream().flatMap(List::stream).collect(Collectors.toList()))
                .flatMapMany(documents -> {
                    var augmentedQuery = query;

                    for (QueryAugmenter augmenter : augmenters) {
                        augmentedQuery = augmenter.augment(augmentedQuery, documents);
                    }

                    return streamingGenerator.streamGeneration(augmentedQuery);
                });
    }
}
