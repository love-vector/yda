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
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.StreamingGenerator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.util.ContentUtil;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class DefaultStreamingRag implements StreamingRag<RagRequest, RagResponse> {

    private final List<Retriever<RagRequest, RagContext>> retrievers;

    private final List<Augmenter<RagRequest, RagContext>> augmenters;

    private final StreamingGenerator<RagRequest, RagResponse> generator;

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

    protected Mono<String> mergeContexts(final List<RagContext> contexts) {
        return Flux.fromStream(contexts.parallelStream())
                .map(ragContext -> String.join(ContentUtil.SENTENCE_SEPARATOR, ragContext.getKnowledge()))
                .collect(Collectors.joining(ContentUtil.SENTENCE_SEPARATOR));
    }
}
