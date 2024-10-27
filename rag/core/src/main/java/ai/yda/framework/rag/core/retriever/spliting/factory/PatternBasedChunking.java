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
package ai.yda.framework.rag.core.retriever.spliting.factory;

import java.util.List;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.model.Node;
import ai.yda.framework.rag.core.retriever.spliting.strategy.ChunkStrategy;

/**
 * Provides a pattern-based chunking mechanism that selects the appropriate chunking strategy based on the
 * given {@link ChunkingAlgorithm}. This allows for flexible and dynamic chunking of document data.
 *
 * @author Bogdan Synenko
 * @since 0.2.0
 */
public class PatternBasedChunking {

    /**
     * Factory for generating the appropriate chunking strategy.
     */
    private final ChunkStrategyFactory chunkStrategyFactory;

    /**
     * Default constructor for creating a {@link PatternBasedChunking} instance.
     * Initializes the chunking strategy factory.
     */
    public PatternBasedChunking() {
        this.chunkStrategyFactory = new ChunkStrategyFactory();
    }

    /**
     * Splits a list of documents into chunks based on the selected chunking algorithm.
     *
     * @param chunkingAlgorithm the algorithm to determine the chunking strategy.
     * @param documents         the list of documents to be chunked.
     * @return a list of {@link Node} objects that represent the split document data.
     */
    public List<Node> nodeList(final ChunkingAlgorithm chunkingAlgorithm, final List<DocumentData> documents) {
        ChunkStrategy strategy = chunkStrategyFactory.getStrategy(chunkingAlgorithm);
        return strategy.splitChunks(documents);
    }
}
