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
package ai.yda.framework.rag.retriever.google_drive.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.SummaryMetadataEnricher;

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentContentEntity;
import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;

@Slf4j
public class DocumentSummaryService {

    private final ChatModel chatModel;

    public DocumentSummaryService(final ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public List<Document> summarizeDocuments(final List<DocumentMetadataEntity> metadataDocuments) {
        var transformDocuments = transformDocument(metadataDocuments);
        var enrichedDocuments =
                new SummaryMetadataEnricher(chatModel, List.of(SummaryMetadataEnricher.SummaryType.CURRENT));
        var documentSummary = enrichedDocuments.apply(transformDocuments);
        return prepareDocuments(documentSummary);
    }

    private List<Document> transformDocument(final List<DocumentMetadataEntity> metadataDocuments) {
        return metadataDocuments.stream()
                .map(metadataDocument -> {
                    var documentContent = metadataDocument.getDocumentContents().stream()
                            .map(DocumentContentEntity::getChunkContent)
                            .collect(Collectors.joining(""));
                    return Document.builder()
                            .text(documentContent + metadataDocument.getDescription() + metadataDocument.getName())
                            .id(metadataDocument.getDocumentId())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<Document> prepareDocuments(final List<Document> documents) {
        return documents.stream()
                .map(document -> document.mutate()
                        .text(document.getMetadata()
                                .getOrDefault("section_summary", "")
                                .toString())
                        .metadata(Map.of())
                        .build())
                .toList();
    }
}
