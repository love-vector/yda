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

import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

@Slf4j
public abstract class BaseRetriever implements DocumentRetriever {

    private final List<QueryTransformer> queryTransformers;

    protected BaseRetriever() {
        this(Collections.emptyList());
    }

    protected BaseRetriever(final List<QueryTransformer> queryTransformers) {
        this.queryTransformers = queryTransformers;
    }

    public List<Document> transformAndRetrieve(Query query) {
        for (var queryTransformer : queryTransformers) {
            query = queryTransformer.transform(query);
            if (log.isDebugEnabled()) {
                log.debug("Transformed query: {}", query);
            }
        }
        return retrieve(query);
    }
}
