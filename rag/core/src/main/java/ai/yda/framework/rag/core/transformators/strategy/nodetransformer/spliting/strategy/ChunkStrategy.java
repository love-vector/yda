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
package ai.yda.framework.rag.core.transformators.strategy.nodetransformer.spliting.strategy;

import java.util.List;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.model.Node;

/**
 * Defines the strategy for chunking documents into smaller, manageable parts based on different criteria.
 *
 * @author Bogdan Synenko
 * @since 0.2.0
 */
public interface ChunkStrategy {

    /**
     * Splits a list of documents into smaller chunks based on the chunking strategy.
     *
     * @param documents the list of documents to be split into chunks.
     * @return a list of chunks created from the input documents.
     */
    List<Node> splitChunks(List<DocumentData> documents);
}
