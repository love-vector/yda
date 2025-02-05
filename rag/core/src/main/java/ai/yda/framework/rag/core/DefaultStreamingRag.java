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
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import ai.yda.framework.rag.core.generator.StreamingGenerator;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.util.ContentUtil;
import ai.yda.framework.rag.core.util.StreamingRequestTransformer;

/**
 * Default implementation of the Retrieval-Augmented Generation (RAG) process in a streaming manner.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
public class DefaultStreamingRag implements StreamingRag<RagRequest, RagResponse> {

    private final List<DocumentRetriever> retrievers;

    private final List<QueryAugmenter> augmenters;

    /**
     * The {@link StreamingGenerator} responsible for generating the final {@link RagResponse} in a streaming manner.
     */
    private final StreamingGenerator<RagRequest, RagResponse> streamingGenerator;

    /**
     * The list of {@link StreamingRequestTransformer} instances used to transform the incoming {@link RagRequest}.
     */
    private final List<StreamingRequestTransformer<RagRequest>> requestTransformers;

    public DefaultStreamingRag(
            final List<DocumentRetriever> retrievers,
            final List<QueryAugmenter> augmenters,
            final StreamingGenerator<RagRequest, RagResponse> streamingGenerator,
            final List<StreamingRequestTransformer<RagRequest>> requestTransformers) {
        this.retrievers = retrievers;
        this.augmenters = augmenters;
        this.streamingGenerator = streamingGenerator;
        this.requestTransformers = requestTransformers;
    }

    @Override
    public Flux<RagResponse> streamRag(final RagRequest request) {
        return Flux.fromIterable(requestTransformers)
                .reduce(
                        Mono.just(request),
                        (monoRequest, transformer) -> monoRequest.flatMap(transformer::transformRequest))
                .flatMap(monoRequest -> monoRequest)
                .flatMapMany(transformedRequest -> Flux.fromIterable(retrievers)
                        .flatMap(retriever -> Mono.fromCallable(
                                        () -> retriever.retrieve(new Query(transformedRequest.getQuery())))
                                .subscribeOn(Schedulers.boundedElastic()))
                        .collectList()
                        .flatMap(documents -> {
                            var documentsList =
                                    documents.stream().flatMap(List::stream).toList();
                            for (var augmenter : augmenters) {
                                documentsList = Stream.of(augmenter.augment(
                                                new Query(transformedRequest.getQuery()), documentsList))
                                        .map(query -> new Document(query.text()))
                                        .toList();
                            }
                            return Mono.just(documentsList);
                        })
                        .flatMap(this::mergeDocuments)
                        .flatMapMany(document -> streamingGenerator.streamGeneration(transformedRequest, document)));
    }

    protected Mono<String> mergeDocuments(final List<Document> documents) {
        return Flux.fromStream(documents.parallelStream())
                .map(Document::getFormattedContent)
                .collect(Collectors.joining(ContentUtil.SENTENCE_SEPARATOR));
    }
}
