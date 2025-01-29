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

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentContentDTO;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentMetadataDTO;

@Slf4j
public class ChunkingService {

    public List<String> chunkList(Document document) {
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(250, 100, 5, 10000, false);
        return tokenTextSplitter.split(document).stream().map(Document::getText).collect(Collectors.toList());
    }

    public DocumentMetadataDTO processContent(
            final DocumentMetadataDTO documentMetadataDTO, final String filesExtension) {
        if (filesExtension.contains("pdf") || filesExtension.contains("docx") || filesExtension.contains("docxx")) {
            var incomeObject = documentMetadataDTO.getDocumentContents().get(0); // достаём текущий обьект
            var document = documentMetadataDTO
                    .getDocumentContents()
                    .get(0); // достаём текущий документ который нужно чанкировать
            var documentForSplitting = document.getChunkContent(); // достаём нужный контент
            var transformDocument = new Document(documentForSplitting); // трансформируем в нужный формат
            var splitOntoChunks = chunkList(transformDocument); // получаем List<String> в нужном формате
            var transformEveryChunks = splitOntoChunks.stream()
                    .map(node -> {
                        return DocumentContentDTO.builder()
                                .documentMetadataId(documentMetadataDTO.getDocumentId())
                                .chunkName(documentMetadataDTO.getName())
                                .contentId(incomeObject.getContentId())
                                .chunkContent(node)
                                .build();
                    })
                    .toList(); // формируем новый список List<DocumentContentDto> из чанков
            documentMetadataDTO.setDocumentContents(transformEveryChunks);
        }
        return documentMetadataDTO;
    }
}
