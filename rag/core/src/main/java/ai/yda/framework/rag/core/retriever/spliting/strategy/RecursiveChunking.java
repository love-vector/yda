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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.model.Node;

public class RecursiveChunking implements ChunkStrategy {

    private final List<Pattern> patterns;

    public RecursiveChunking(final List<String> patterns) {
        this.patterns = new ArrayList<>();
        patterns.forEach(regex -> this.patterns.add(Pattern.compile(regex)));
    }

    @Override
    public List<Node> splitChunks(final List<DocumentData> documentDataList) {
        List<Node> nodeList = new ArrayList<>();
        var nodeIndex = new AtomicInteger(0);

        documentDataList.forEach(document -> {
            var documentContent = document.getContent();
            var documentId = document.getMetadata().get("documentId").toString();

            patterns.forEach(pattern -> {
                var matcher = pattern.matcher(documentContent);
                var lastMatchEnd = 0;

                while (matcher.find()) {
                    var nodeContent = documentContent
                            .substring(lastMatchEnd, matcher.start())
                            .trim();
                    if (!nodeContent.isEmpty()) {
                        nodeList.add(new Node(
                                nodeContent, Map.of("index", nodeIndex.getAndIncrement(), "documentId", documentId)));
                    }
                    lastMatchEnd = matcher.end();
                }

                if (lastMatchEnd < documentContent.length()) {
                    nodeList.add(new Node(
                            documentContent.substring(lastMatchEnd).trim(),
                            Map.of("index", nodeIndex.getAndIncrement(), "documentId", documentId)));
                }
            });
        });

        return nodeList;
    }
}
