package ai.yda.framework.rag.retriever.google_drive.service;

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentContentEntity;
import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.SummaryMetadataEnricher;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DocumentSummaryService {
    private final ChatModel chatModel;

    public DocumentSummaryService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public List<Document> summarizeDocuments(final List<DocumentMetadataEntity> metadataDocuments) {
        var transformDocuments = transformDocument(metadataDocuments);
        var enrichedDocuments = new SummaryMetadataEnricher(chatModel, List.of(SummaryMetadataEnricher.SummaryType.CURRENT));
        var documentSummary =  enrichedDocuments.apply(transformDocuments);
        return prepareDocuments(documentSummary);
    }

    private List<Document> transformDocument(final List<DocumentMetadataEntity> metadataDocuments) {
        return metadataDocuments.stream().map(metadataDocument -> {
            var documentContent = metadataDocument.getDocumentContents().stream()
                    .map(DocumentContentEntity::getChunkContent).collect(Collectors.joining(""));
            return new Document(
                    documentContent + metadataDocument.getDescription() + metadataDocument.getName())
                    .mutate().id(metadataDocument.getDocumentId())

                    .build();
        }).collect(Collectors.toList());
    }

    private List<Document> prepareDocuments(final List<Document> documents) {
        return documents.stream().map(document -> document.mutate()
                .text(document.getMetadata().getOrDefault("section_summary","").toString())
                .metadata(Map.of())
                .build()).toList();
    }
}