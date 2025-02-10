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

import lombok.AccessLevel;
import lombok.Getter;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.QueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagResponse;

/**
 * Default implementation of the Retrieval-Augmented Generation (RAG) process.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@Getter(AccessLevel.PROTECTED)
public class DefaultRag implements Rag<Query, RagResponse> {
    /**
     * The list of {@link DocumentRetriever} instances used to retrieve {@link Document}.
     */
    private final List<DocumentRetriever> retrievers;

    /**
     * The list of {@link QueryAugmenter} instances used to modify or enhance the retrieved {@link Query}.
     */
    private final List<QueryAugmenter> augmenters;

    /**
     * The {@link Generator} instance responsible for generating the final {@link RagResponse}.
     */
    private final Generator<Query, RagResponse> generator;

    /**
     * Constructs a new {@link DefaultRag} instance.
     *
     * @param retrievers the list of {@link DocumentRetriever} objects to retrieve {@link Document} data.
     * @param augmenters the list of {@link QueryAugmenter} objects to augment the retrieved Contexts.
     * @param generator  the {@link Generator} used to generate the {@link RagResponse}.
     */
    public DefaultRag(
            final List<DocumentRetriever> retrievers,
            final List<QueryAugmenter> augmenters,
            final Generator<Query, RagResponse> generator) {
        this.retrievers = retrievers;
        this.augmenters = augmenters;
        this.generator = generator;
    }

    /**
     * Executes the Retrieval-Augmented Generation (RAG) process by:
     * <ul>
     *     <li>Transforming the initial {@link Query}
     *     <li>Retrieving relevant {@link Document} from the {@link DocumentRetriever} instances.</li>
     *     <li>Augmenting the retrieved Contexts using the provided {@link QueryAugmenter} instances.</li>
     *     <li>
     *         Generating the final {@link RagResponse} using the {@link Generator}, based on the augmented Contexts.
     *     </li>
     * </ul>
     *
     * @param query the {@link Query} to process.
     * @return the generated {@link RagResponse}.
     */
    @Override
    public RagResponse doRag(final Query query) {
        var documents = retrievers.parallelStream()
                .flatMap(retriever -> retriever.retrieve(query).stream())
                .toList();
        var augmentedQuery = augmenters.stream()
                .reduce(query, (current, augmenter) -> augmenter.augment(current, documents), (q1, q2) -> q2);
        return generator.generate(augmentedQuery);
    }
}
