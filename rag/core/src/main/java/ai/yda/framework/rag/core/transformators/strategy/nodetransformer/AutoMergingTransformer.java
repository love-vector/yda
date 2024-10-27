package ai.yda.framework.rag.core.transformators.strategy.nodetransformer;

import ai.yda.framework.rag.core.model.DocumentData;
import ai.yda.framework.rag.core.model.Node;
import ai.yda.framework.rag.core.retriever.spliting.factory.ChunkingAlgorithm;
import ai.yda.framework.rag.core.retriever.spliting.factory.PatternBasedChunking;
import ai.yda.framework.rag.core.transformators.strategy.NodeTransformerStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AutoMergingTransformer implements NodeTransformerStrategy<DocumentData> {

    @Override
    public List<DocumentData> processDataList(List<DocumentData> dataList, ChunkingAlgorithm chunkingAlgorithm) {
        PatternBasedChunking patternBasedChunking = new PatternBasedChunking();

        var nodeList = patternBasedChunking.nodeList(chunkingAlgorithm, dataList);
        var enrichedNodes = enrichContext(nodeList);
        var autoMergeNodes = autoMergeNodes(enrichedNodes);

        return transformToDocumentData(autoMergeNodes);
    }

    // Step 2: Context Enrichment
    private List<Node> enrichContext(List<Node> nodes) {
        return nodes.stream().map(node -> {
            var updatedMetadata = new HashMap<>(node.getMetadata());
            var nodeIndex = Integer.parseInt(node.getMetadata().get("Index").toString());
            updatedMetadata.put("startPosition", nodeIndex * 100);
            updatedMetadata.put("endPosition", (nodeIndex + 1) * 100);
            updatedMetadata.put("contentType", "paragraph");
            updatedMetadata.put("keywords", List.of("AI", "retrieval"));
            return node.toBuilder().metadata(updatedMetadata).build();
        }).collect(Collectors.toList());
    }

    // Step 3: Auto-Merging Nodes
    private List<Node> autoMergeNodes(List<Node> enrichedNodes) {
        List<Node> mergedNodes = new ArrayList<>();
        var parentNodeMap = enrichedNodes.stream().collect(Collectors.groupingBy(node -> (String) node.getMetadata().get("documentId")));

        parentNodeMap.forEach((documentId, childNodes) -> {
            for (int i = 0; i < childNodes.size(); i++) {
                var currentNode = childNodes.get(i);
                if (i < childNodes.size() - 1) {
                    var nextNode = childNodes.get(i + 1);
                    if (Math.abs((int) nextNode.getMetadata().get("index") - (int) currentNode.getMetadata().get("index")) == 1) {
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


    //Transform to DocumentData for common structure
    private List<DocumentData> transformToDocumentData(List<Node> autoMergeNodes) {
        Map<String, List<Node>> documentNodesMap = autoMergeNodes.stream().collect(Collectors.groupingBy(node -> (String) node.getMetadata().get("documentId")));
        List<DocumentData> documentDataList = new ArrayList<>();

        documentNodesMap.forEach((documentId, nodeList) -> {
            var mergedContent = nodeList.stream().map(Node::getContent).collect(Collectors.joining(" "));
            var metadata = nodeList.get(0).getMetadata();
            documentDataList.add(new DocumentData(mergedContent, metadata));
        });

        return documentDataList;
    }
}