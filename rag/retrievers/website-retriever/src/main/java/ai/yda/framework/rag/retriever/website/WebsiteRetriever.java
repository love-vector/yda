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
package ai.yda.framework.rag.retriever.website;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.Retriever;
import ai.yda.framework.rag.retriever.website.service.WebsiteService;

@Slf4j
public class WebsiteRetriever implements Retriever<RagRequest, RagContext> {
    private final VectorStore vectorStore;
    private final String sitemapUrl;
    private final Integer topK;
    private final WebsiteService websiteService = new WebsiteService();

    public WebsiteRetriever(
            final VectorStore vectorStore,
            final String sitemapUrl,
            final Integer topK,
            final Boolean isProcessingEnabled) {
        this.vectorStore = vectorStore;
        this.sitemapUrl = sitemapUrl;
        this.topK = topK;

        if (isProcessingEnabled) {
            processWebsite();
        }
    }

    @Override
    public RagContext retrieve(final RagRequest request) {
        return RagContext.builder()
                .knowledge(
                        vectorStore
                                .similaritySearch(
                                        SearchRequest.query(request.getQuery()).withTopK(topK))
                                .parallelStream()
                                .map(Document::getContent)
                                .toList())
                .build();
    }

    private void processWebsite() {
        var pageDocuments = websiteService.createChunkDocumentsFromSitemapUrl(sitemapUrl);
        vectorStore.add(pageDocuments);
    }
}
