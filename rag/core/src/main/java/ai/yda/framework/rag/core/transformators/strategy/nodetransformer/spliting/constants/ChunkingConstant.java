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
package ai.yda.framework.rag.core.transformators.strategy.nodetransformer.spliting.constants;

/**
 * This class defines constants that are commonly used for chunking strategies within the framework.
 * These constants control chunk size limits, regular expression patterns for chunking,
 * and parameters for sliding window chunking mechanisms.
 *
 * <p>Constants like {@code CHUNK_MAX_LENGTH}, {@code REGEX_PATTERN}, and {@code WINDOW_SIZE}
 * can be reused across different chunking strategies to ensure consistent behavior.</p>
 *
 * @author Bogdan Synenko
 * @since 0.2.0
 */
public class ChunkingConstant {

    /**
     * The maximum length of a chunk in terms of characters.
     * This value is used to split large text into smaller chunks, ensuring that no chunk exceeds
     * this length. It is particularly useful in fixed-length chunking strategies.
     */
    public static final int CHUNK_MAX_LENGTH = 1000;

    /**
     * The regular expression pattern used to split text based on sentence boundaries.
     * This pattern detects the end of sentences by looking for punctuation marks like periods (.), exclamation marks (!),
     * or question marks (?), followed by one or more spaces.
     * It is used in strategies that rely on sentence-based chunking.
     */
    public static final String REGEX_PATTERN = "[.!?]\\s+";

    /**
     * The size of the window used in sliding window chunking strategies.
     * This defines how many tokens (words or characters) are included in each window when splitting the text.
     * Sliding window strategies create overlapping chunks based on this window size.
     */
    public static final int WINDOW_SIZE = 100;

    /**
     * The step size used in sliding window chunking strategies.
     * This value defines how much the window moves forward with each iteration.
     * A smaller step will result in more overlap between chunks, while a larger step will reduce overlap.
     */
    public static final int WINDOW_STEP = 50;
}
