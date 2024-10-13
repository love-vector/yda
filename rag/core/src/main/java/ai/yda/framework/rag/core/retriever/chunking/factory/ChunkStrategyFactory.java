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
package ai.yda.framework.rag.core.retriever.chunking.factory;

import java.util.List;

import ai.yda.framework.rag.core.retriever.chunking.constants.ChunkingConstants;
import ai.yda.framework.rag.core.retriever.chunking.strategy.ChunkStrategy;
import ai.yda.framework.rag.core.retriever.chunking.strategy.FixedLengthWordChunking;
import ai.yda.framework.rag.core.retriever.chunking.strategy.RegexChunking;
import ai.yda.framework.rag.core.retriever.chunking.strategy.SlidingWindowChunking;

/**
 * Factory class for creating different types of {@link ChunkStrategy} based on the provided {@link ChunkingAlgorithm}.
 * The factory supports fixed-length, sentence-based, and sliding window chunking strategies.
 *
 * @author Bogdan Synenko
 * @since 0.2.0
 */
public class ChunkStrategyFactory {

    /**
     * Returns the appropriate chunking strategy based on the provided algorithm.
     *
     * @param chunkingAlgorithm the algorithm type used to determine the chunking strategy.
     * @return the {@link ChunkStrategy} that corresponds to the selected chunking algorithm.
     */
    public ChunkStrategy getStrategy(final ChunkingAlgorithm chunkingAlgorithm) {
        switch (chunkingAlgorithm) {
            case FIXED:
                return new FixedLengthWordChunking(ChunkingConstants.CHUNK_MAX_LENGTH);
            case SENTENCES:
                return new RegexChunking(List.of(ChunkingConstants.REGEX_PATTERN));
            case WINDOW:
                return new SlidingWindowChunking(ChunkingConstants.WINDOW_SIZE, ChunkingConstants.WINDOW_STEP);
            default:
                throw new RuntimeException("Unknown chunking algorithm: " + chunkingAlgorithm);
        }
    }
}
