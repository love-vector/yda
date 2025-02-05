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

import lombok.AccessLevel;
import lombok.Getter;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.util.RequestTransformer;

/**
 * Default implementation of the Retrieval-Augmented Generation (RAG) process.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@Getter(AccessLevel.PROTECTED)
public class DefaultRag implements Rag<RagRequest, RagResponse> {

    private final List<DocumentRetriever> retrievers;

    private final List<QueryAugmenter> augmenters;

    /**
     * The {@link Generator} instance responsible for generating the final {@link RagResponse}.
     */
    private final Generator<RagRequest, RagResponse> generator;

    /**
     * The list of {@link RequestTransformer} instances used to transform the incoming {@link RagRequest}.
     */
    private final List<RequestTransformer<RagRequest>> requestTransformers;

    public DefaultRag(
            final List<DocumentRetriever> retrievers,
            final List<QueryAugmenter> augmenters,
            final Generator<RagRequest, RagResponse> generator,
            final List<RequestTransformer<RagRequest>> requestTransformers) {
        this.retrievers = retrievers;
        this.augmenters = augmenters;
        this.generator = generator;
        this.requestTransformers = requestTransformers;
    }

    @Override
    public RagResponse doRag(final RagRequest request) {
        //        var transformingRequest = request;
        //        for (RequestTransformer<RagRequest> requestTransformer : requestTransformers) {
        //            transformingRequest = requestTransformer.transformRequest(transformingRequest);
        //        }
        //        var transformedRequest = transformingRequest;
        var documents = retrievers.parallelStream()
                .flatMap(retriever -> retriever.retrieve(new Query(request.getQuery())).stream())
                .toList();
        //                .map(retriever -> retriever.retrieve(transformedRequest))

        for (var augmenter : augmenters) {
            documents = Stream.of(augmenter.augment(new Query(request.getQuery()), documents))
                    .map(query -> new Document(query.text()))
                    .collect(Collectors.toList());
            //            documents = augmenter.augment(transformedRequest, documents);
        }
        return generator.generate(request, mergeDocuments(documents));
        //        return generator.generate(transformedRequest, mergeContexts(documents));
    }

    /**
     * Merges the Knowledge from the list of {@link Document} objects into a single string. Each piece of Knowledge is
     * separated by a point character.
     *
     * @param documents the list of {@link Document} objects containing Knowledge data.
     * @return a string that combines all pieces of Knowledge from the Contexts.
     */
    protected String mergeDocuments(final List<Document> documents) {
        return documents.parallelStream().map(Document::getFormattedContent).collect(Collectors.joining("\n\n"));
    }
}
