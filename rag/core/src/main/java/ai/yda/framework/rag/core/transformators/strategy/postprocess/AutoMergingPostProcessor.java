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
package ai.yda.framework.rag.core.transformators.strategy.postprocess;

import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.transformators.strategy.NodePostProcessorStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AutoMergingPostProcessor implements NodePostProcessorStrategy {
    @Override
    public List<RagContext> retrieveRagContext(List<RagContext> ragContext) {
        var mergedData = mergeData(ragContext);
        return filterRelevantContexts(mergedData);
    }

    private List<RagContext> mergeData(List<RagContext> retrievedDocuments) {
        Map<String, RagContext> mergedResults = new HashMap<>();

        retrievedDocuments.forEach(document -> {
            var documentId = document.getMetadata().get("documentId").toString();

            if (mergedResults.containsKey(documentId)) {
                var existingContent = mergedResults.get(documentId);
                var mergeKnowledges = new ArrayList<>(existingContent.getKnowledge());
                mergeKnowledges.addAll(document.getKnowledge());

                mergedResults.put(
                        documentId,
                        existingContent.toBuilder().knowledge(mergeKnowledges).build());
            } else {
                mergedResults.put(documentId, document);
            }
        });

        return new ArrayList<>(mergedResults.values());
    }

    private List<RagContext> filterRelevantContexts(List<RagContext> mergedData) {
        return mergedData.stream().filter(this::isRelevant).collect(Collectors.toList());
    }

    private boolean isRelevant(RagContext context) {
        return !context.getKnowledge().isEmpty();
    }

}
