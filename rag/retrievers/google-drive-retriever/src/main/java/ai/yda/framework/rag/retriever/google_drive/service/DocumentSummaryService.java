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
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.transformer.SummaryMetadataEnricher;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentContentDTO;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentMetadataDTO;

@Slf4j
public class DocumentSummaryService {


    private final ChatModel chatModel;

    public DocumentSummaryService(final ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String summarizeDocument(final DocumentMetadataDTO metadataDocument) {
        var documentSummaryInstruction =
                """
        Provide a summary of the attached document by highlighting its key points.
        Focus on the main arguments, supporting evidence, and conclusions drawn within the document.
        The summary should be concise yet comprehensive, capturing the essence of the document's content.
        For each document, ensure the following details are included at the beginning of the summary:
        - File Name: Include the file name or mention 'File name is missing' if unavailable.
        - Document Description: Include the document description or mention 'Description is missing' if unavailable.

        Document Content:
        {context_str}

        Output Format:
        - Present the summary in paragraph form, using bullet points if necessary to delineate distinct ideas or sections.
        """;
        var transformDocuments = transformDocument(metadataDocument);
        var documentsEnricher = new SummaryMetadataEnricher(
                chatModel,
                List.of(SummaryMetadataEnricher.SummaryType.CURRENT),
                documentSummaryInstruction,
                MetadataMode.ALL);
        var documentSummary = documentsEnricher.apply(List.of(transformDocuments));
        return extractSummary(documentSummary.get(0));
    }

    private Document transformDocument(final DocumentMetadataDTO metadataDocument) {
        return Document.builder()
                .text("file name: " + metadataDocument.getName()
                        + "\n document description: " + metadataDocument.getDescription()
                        + "\ndocument content: "
                        + metadataDocument.getDocumentContents().stream()
                                .map(DocumentContentDTO::getChunkContent)
                                .collect(Collectors.joining("")))
                .id(metadataDocument.getDocumentId())
                .build();
    }

    private String extractSummary(final Document document) {
        return document.getMetadata().getOrDefault("section_summary", "").toString();
    }
}
