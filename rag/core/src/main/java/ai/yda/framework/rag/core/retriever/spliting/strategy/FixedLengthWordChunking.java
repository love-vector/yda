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
package ai.yda.framework.rag.core.retriever.spliting.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.model.Node;

public class FixedLengthWordChunking implements ChunkStrategy {

    private final int nodeSize;

    public FixedLengthWordChunking(final int nodeSize) {
        this.nodeSize = nodeSize;
    }

    @Override
    public List<Node> splitChunks(final List<DocumentData> documentDataList) {
        var nodeIndex = new AtomicInteger(0);

        return documentDataList.stream()
                .flatMap(document -> {
                    var documentContent = document.getContent();
                    var documentId = document.getMetadata().get("documentId").toString();
                    var parts = (documentContent.length() + nodeSize - 1) / nodeSize;

                    return IntStream.range(0, parts).mapToObj(node -> {
                        var start = node * nodeSize;
                        var nodeContent = documentContent
                                .substring(start, Math.min(start + nodeSize, documentContent.length()))
                                .trim();
                        var nodeMetadata = new HashMap<>(document.getMetadata());
                        nodeMetadata.put("index", nodeIndex.getAndIncrement());
                        nodeMetadata.put("documentId", documentId);

                        return new Node(nodeContent, nodeMetadata);
                    });
                })
                .collect(Collectors.toList());
    }
}
