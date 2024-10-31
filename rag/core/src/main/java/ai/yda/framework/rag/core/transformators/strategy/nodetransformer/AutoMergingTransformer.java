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
package ai.yda.framework.rag.core.transformators.strategy.nodetransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.model.Node;
import ai.yda.framework.rag.core.transformators.factory.ChunkingAlgorithm;
import ai.yda.framework.rag.core.transformators.factory.PatternBasedChunking;
import ai.yda.framework.rag.core.transformators.strategy.NodeTransformerStrategy;

public class AutoMergingTransformer implements NodeTransformerStrategy<DocumentData> {

    @Override
    public List<DocumentData> processDataList(
            final List<DocumentData> dataList, final ChunkingAlgorithm chunkingAlgorithm) {
        PatternBasedChunking patternBasedChunking = new PatternBasedChunking();

        var nodeList = patternBasedChunking.nodeList(chunkingAlgorithm, dataList);
        var enrichedNodes = enrichContext(nodeList);
        var autoMergeNodes = autoMergeNodes(enrichedNodes);

        return transformToDocumentData(autoMergeNodes);
    }

    private List<Node> enrichContext(final List<Node> nodes) {
        return nodes.stream()
                .map(node -> {
                    var updatedMetadata = new HashMap<>(node.getMetadata());
                    var nodeIndex =
                            Integer.parseInt(node.getMetadata().get("Index").toString());
                    updatedMetadata.put("startPosition", nodeIndex * 100);
                    updatedMetadata.put("endPosition", (nodeIndex + 1) * 100);
                    return node.toBuilder().metadata(updatedMetadata).build();
                })
                .collect(Collectors.toList());
    }

    private List<Node> autoMergeNodes(final List<Node> enrichedNodes) {
        List<Node> mergedNodes = new ArrayList<>();
        var parentNodeMap = enrichedNodes.stream().collect(Collectors.groupingBy(node ->
                (String) node.getMetadata().get("documentId")));

        parentNodeMap.forEach((documentId, childNodes) -> {
            for (int i = 0; i < childNodes.size(); i++) {
                var currentNode = childNodes.get(i);
                if (i < childNodes.size() - 1) {
                    var nextNode = childNodes.get(i + 1);
                    if (Math.abs((int) nextNode.getMetadata().get("index")
                                    - (int) currentNode.getMetadata().get("index"))
                            == 1) {
                        var mergedContent = currentNode.getContent() + " " + nextNode.getContent();
                        var mergedMetadata = new HashMap<>(currentNode.getMetadata());
                        mergedMetadata.putAll(nextNode.getMetadata());
                        mergedNodes.add(new Node(mergedContent, mergedMetadata));
                        i++;
                    } else {
                        mergedNodes.add(currentNode);
                    }
                } else {
                    mergedNodes.add(currentNode);
                }
            }
        });

        return mergedNodes;
    }

    private List<DocumentData> transformToDocumentData(final List<Node> autoMergeNodes) {
        Map<String, List<Node>> documentNodesMap = autoMergeNodes.stream().collect(Collectors.groupingBy(node ->
                (String) node.getMetadata().get("documentId")));
        List<DocumentData> documentDataList = new ArrayList<>();

        documentNodesMap.forEach((documentId, nodeList) -> {
            var mergedContent = nodeList.stream().map(Node::getContent).collect(Collectors.joining(" "));
            var metadata = nodeList.get(0).getMetadata();
            documentDataList.add(new DocumentData(mergedContent, metadata));
        });

        return documentDataList;
    }
}
