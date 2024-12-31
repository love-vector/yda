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
package ai.yda.framework.rag.retriever.google_drive;

import java.util.Collections;

import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.retriever.Retriever;

@Slf4j
public class GoogleDriveRetriever implements Retriever<RagRequest, RagContext> {

    /**
     * The number of top results to retrieve from the Vector Store.
     */
    private final Integer topK;

    public GoogleDriveRetriever(final @NonNull Integer topK, final @NonNull Boolean isProcessingEnabled) {

        if (topK <= 0) {
            throw new IllegalArgumentException("TopK must be a positive number.");
        }
        this.topK = topK;

        if (isProcessingEnabled) {
            log.info("Starting Google Drive retriever...");
        }
    }

    @Override
    public RagContext retrieve(final RagRequest request) {
        return RagContext.builder().knowledge(Collections.emptyList()).build();
    }
}
