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
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import ai.yda.framework.rag.core.generator.Generator;

/**
 * Default implementation of the Retrieval-Augmented Generation (RAG) process.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@Getter(AccessLevel.PROTECTED)
public class DefaultRag implements Rag {

    private final List<DocumentRetriever> retrievers;

    private final List<QueryAugmenter> augmenters;

    private final Generator generator;

    public DefaultRag(
            final List<DocumentRetriever> retrievers,
            final List<QueryAugmenter> augmenters,
            final Generator generator) {
        this.retrievers = retrievers;
        this.augmenters = augmenters;
        this.generator = generator;
    }

    @Override
    public Query doRag(final Query request) {

        var documents = retrievers.parallelStream()
                .flatMap(retriever -> retriever.retrieve(request).stream())
                .toList();

        for (var augmenter : augmenters) {
            augmenter.augment(request, documents);
        }
        return generator.generate(request.mutate()
                .context(Map.of("context", mergeDocuments(documents)))
                .build());
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
