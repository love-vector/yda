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

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.model.Node;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SlidingWindowChunking implements ChunkStrategy {

    private final int windowSize;
    private final int step;

    public SlidingWindowChunking(final int windowSize, final int step) {
        this.windowSize = windowSize;
        this.step = step;
    }

    @Override
    public List<Node> splitChunks(final List<DocumentData> documentDataList) {
        var nodeIndex = new AtomicInteger(0);

        return documentDataList.stream()
                .flatMap(document -> {
                    var documentContent = document.getContent();
                    var documentId = document.getMetadata().get("documentId").toString();
                    String[] words = documentContent.split("\\s+");
                    int parts = (words.length - windowSize + step) / step;

                    return IntStream.range(0, parts).mapToObj(node -> {
                        var start = node * step;
                        var nodeContent = Stream.of(words)
                                .skip(start)
                                .limit(windowSize)
                                .collect(Collectors.joining(" ")).trim();
                        var nodeMetadata = new HashMap<>(document.getMetadata());
                        nodeMetadata.put("index", nodeIndex.getAndIncrement());
                        nodeMetadata.put("documentId", documentId);

                        return new Node(nodeContent, nodeMetadata);
                    });
                })
                .collect(Collectors.toList());
    }
}