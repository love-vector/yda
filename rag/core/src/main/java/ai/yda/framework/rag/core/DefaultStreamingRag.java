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

import ai.yda.framework.rag.core.generator.StreamingGenerator;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.util.ContentUtil;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default implementation of the Retrieval-Augmented Generation (RAG) process in a streaming manner.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
public class DefaultStreamingRag implements StreamingRag<RagResponse, Query> {

    private final List<DocumentRetriever> retrievers;

    private final List<QueryAugmenter> augmenters;

    private final StreamingGenerator<RagResponse, Query> streamingGenerator;

    public DefaultStreamingRag(
            final List<DocumentRetriever> retrievers,
            final List<QueryAugmenter> augmenters,
            final StreamingGenerator<RagResponse, Query> streamingGenerator) {
        this.retrievers = retrievers;
        this.augmenters = augmenters;
        this.streamingGenerator = streamingGenerator;
    }

    @Override
    public Flux<RagResponse> streamRag(final Query request) {
        return Flux.fromIterable(retrievers)
                .flatMap(retriever ->
                        Mono.fromCallable(() -> retriever.retrieve(request)).subscribeOn(Schedulers.boundedElastic()))
                .collectList()
                .map(lists -> lists.stream().flatMap(List::stream).collect(Collectors.toList()))
                .flatMap(documents -> {
                    augmenters.forEach(augmenter -> augmenter.augment(request, documents));
                    return mergeDocuments(documents).map(merged -> new AbstractMap.SimpleEntry<>(documents, merged));
                })
                .flatMapMany(entry -> {
                    String merged = entry.getValue();
                    Query updatedRequest =
                            request.mutate().context(Map.of("context", merged)).build();
                    return streamingGenerator.streamGeneration(updatedRequest);
                });
    }

    protected Mono<String> mergeDocuments(final List<Document> documents) {
        return Flux.fromStream(documents.parallelStream())
                .map(Document::getFormattedContent)
                .collect(Collectors.joining(ContentUtil.SENTENCE_SEPARATOR));
    }
}
