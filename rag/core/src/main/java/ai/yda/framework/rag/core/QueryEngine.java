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

import ai.yda.framework.rag.core.augmenter.Augmenter;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.core.retriever.RetrieverCoordinator;
import ai.yda.framework.rag.core.transformators.factory.NodePostProcessorFactory;
import ai.yda.framework.rag.core.transformators.pipline.PipelineAlgorithm;
import ai.yda.framework.rag.core.util.ContentUtil;
import ai.yda.framework.rag.core.util.RequestTransformer;

/**
 * Default implementation of the Retrieval-Augmented Generation (RAG) process.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@Getter(AccessLevel.PROTECTED)
public class QueryEngine implements Rag<RagRequest, RagResponse> {

    /**
     * The list of {@link Retriever} instances used to retrieve {@link RagContext} based on the {@link RagRequest}.
     */
    private final List<RetrieverCoordinator<DocumentData>> retrieverCoordinators;

    /**
     * The list of {@link Augmenter} instances used to modify or enhance the retrieved {@link RagContext}.
     */
    private final List<Augmenter<RagRequest, RagContext>> augmenters;

    /**
     * The {@link Generator} instance responsible for generating the final {@link RagResponse}.
     */
    private final Generator<RagRequest, RagResponse> generator;

    /**
     * The list of {@link RequestTransformer} instances used to transform the incoming {@link RagRequest}.
     */
    private final List<RequestTransformer<RagRequest>> requestTransformers;

    /**
     * Constructs a new {@link QueryEngine} instance.
     *
     * @param retrieverCoordinators          the list of {@link Retriever} objects to retrieve {@link RagContext} data.
     * @param augmenters          the list of {@link Augmenter} objects to augment the retrieved Contexts.
     * @param generator           the {@link Generator} used to generate the {@link RagResponse}.
     * @param requestTransformers the list of {@link RequestTransformer} objects for transforming the
     *                            {@link RagRequest}.
     */
    public QueryEngine(
            final List<RetrieverCoordinator<DocumentData>> retrieverCoordinators,
            final List<Augmenter<RagRequest, RagContext>> augmenters,
            final Generator<RagRequest, RagResponse> generator,
            final List<RequestTransformer<RagRequest>> requestTransformers) {
        this.retrieverCoordinators = retrieverCoordinators;
        this.augmenters = augmenters;
        this.generator = generator;
        this.requestTransformers = requestTransformers;
    }

    /**
     * Executes the Retrieval-Augmented Generation (RAG) process by:
     * <ul>
     *     <li>Transforming the initial {@link RagRequest} using the provided {@link RequestTransformer} instances.</li>
     *     <li>Retrieving relevant {@link RagContext} from the {@link Retriever} instances.</li>
     *     <li>Augmenting the retrieved Contexts using the provided {@link Augmenter} instances.</li>
     *     <li>
     *         Generating the final {@link RagResponse} using the {@link Generator}, based on the augmented Contexts.
     *     </li>
     * </ul>
     *
     * @param request the {@link RagRequest} to process.
     * @return the generated {@link RagResponse}.
     */
    @Override
    public RagResponse doRag(final RagRequest request) {

        var transformingRequest = request;
        var nodePostProcessorFactory = new NodePostProcessorFactory();
        var strategy = nodePostProcessorFactory.getStrategy(PipelineAlgorithm.AUTO_MERGE);

        for (RequestTransformer<RagRequest> requestTransformer : requestTransformers) {
            transformingRequest = requestTransformer.transformRequest(transformingRequest);
        }

        var transformedRequest = transformingRequest;

        List<RagContext> contexts = retrieverCoordinators.parallelStream()
                .flatMap(retriever -> retriever.retrieve(transformedRequest).stream())
                .collect(Collectors.toUnmodifiableList());

        var processedContexts = strategy.retrieveRagContext(contexts);

        for (var augmenter : augmenters) {
            contexts = augmenter.augment(transformedRequest, contexts);
        }

        return generator.generate(transformedRequest, mergeContexts(processedContexts));
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
                .map(ragContext -> String.join(ContentUtil.SENTENCE_SEPARATOR, ragContext.getContent()))
                .collect(Collectors.joining(ContentUtil.SENTENCE_SEPARATOR));
    }
}