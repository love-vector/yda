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
package ai.yda.framework.rag.core.retriever;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;

import java.util.List;

/**
 * The {@code AbstractQueryEngine} class serves as an abstract base class for
 * engines that are responsible for querying and retrieving context data.
 * It extends the {@link Indexer} class, specializing in indexing
 * {@link DocumentData}, and implements the {@link Retriever} interface for
 * retrieving {@link RagContext} based on a given {@link RagRequest}.
 *
 * <p>This abstract class provides a foundation for more specialized query
 * engines that may interact with various data sources, such as web content,
 * databases, or document repositories. It defines a contract for extracting
 * data from a given source and leaves the implementation details to its subclasses.
 *
 * @author Bogdan Synenko
 * @see Indexer
 * @see Retriever
 * @see RagContext
 * @see RagRequest
 * @since 0.2.0
 */
public abstract class AbstractQueryEngine extends Indexer<DocumentData> implements Retriever<RagRequest, RagContext> {

    /**
     * Extracts data from the given source URL or file path. The implementation
     * of this method is left to subclasses, which may define how to extract
     * relevant content, process it, and convert it into {@link DocumentData}.
     *
     * @param source The URL or path of the source to extract data from.
     * @return A list of {@link DocumentData} representing the extracted content.
     */
    protected abstract List<DocumentData> extractData(String source);
}
