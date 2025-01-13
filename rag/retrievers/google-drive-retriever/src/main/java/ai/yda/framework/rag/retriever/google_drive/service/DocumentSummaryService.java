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

import ai.yda.framework.rag.retriever.google_drive.entity.DocumentContentEntity;
import ai.yda.framework.rag.retriever.google_drive.entity.DocumentMetadataEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.SummaryMetadataEnricher;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DocumentSummaryService {

    private final ChatModel chatModel;

    public DocumentSummaryService(final ChatModel chatModel, final String documentSummaryInstruction) {
        this.chatModel = chatModel;
    }

    public String summarizeDocument(final DocumentMetadataEntity metadataDocuments) {
        var transformDocuments = transformDocument(metadataDocuments);
        var documentsEnricher =
                new SummaryMetadataEnricher(chatModel, List.of(SummaryMetadataEnricher.SummaryType.CURRENT));
        var documentSummary = documentsEnricher.apply(List.of(transformDocuments));
        return extractSummary(documentSummary.get(0));
    }

    private Document transformDocument(final DocumentMetadataEntity metadataDocument) {
        var documentContent = metadataDocument.getDocumentContents().stream()
                .map(DocumentContentEntity::getChunkContent)
                .collect(Collectors.joining(""));
        return Document.builder()
                .text(documentContent + metadataDocument.getDescription() + metadataDocument.getName())
                .id(metadataDocument.getDocumentId())
                .build();
    }

    private String extractSummary(final Document document) {
        return document.getMetadata().getOrDefault("section_summary", "").toString();
    }
}
