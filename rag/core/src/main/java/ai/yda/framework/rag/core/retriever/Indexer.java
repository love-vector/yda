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

import java.util.List;

/**
 * Defines the contract for indexing and processing documents to be stored and retrieved from a data store.
 *
 * @param <DOCUMENT> the type of document to be processed and indexed.
 * @author Bogdan Synenko
 * @since 0.2.0
 */
public abstract class Indexer<DOCUMENT> {
    /**
     * Indexes documents by processing and saving them into the data store.
     * Typically used for initial setup or periodic updates.
     */
    public void index(List<DOCUMENT> extractedData) {
        var chunkedData = process(extractedData);
        save(chunkedData);
    }

    /**
     * Processes a list of documents and returns the processed list.
     *
     * @return the processed list of documents.
     */
    protected abstract List<DOCUMENT> process(List<DOCUMENT> extractedData);

    /**
     * Saves the list of processed documents into the data store.
     *
     * @param documents the list of documents to be saved.
     */
    protected abstract void save(List<DOCUMENT> documents);
}
