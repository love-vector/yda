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
package ai.yda.framework.rag.core.transformators.factory;

/**
 * Enum representing the different chunking algorithms that can be applied to split document content.
 * Each algorithm defines a specific strategy for dividing text into chunks.
 *
 * <ul>
 *   <li>{@code FIXED} - splits content into fixed-length chunks.</li>
 *   <li>{@code SENTENCES} - splits content into chunks based on sentence boundaries.</li>
 *   <li>{@code WINDOW} - uses a sliding window mechanism to split content into overlapping chunks.</li>
 * </ul>
 *
 * @author Bogdan Synenko
 * @since 0.2.0
 */
public enum ChunkingAlgorithm {
    FIXED,
    SENTENCES,
    WINDOW
}
