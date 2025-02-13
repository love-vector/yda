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
package ai.yda.framework.rag.retriever.google_drive.service.processor;

import java.io.InputStream;
import java.util.List;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.Media;
import org.springframework.core.io.InputStreamResource;
import org.springframework.lang.NonNull;
import org.springframework.util.MimeType;

import ai.yda.framework.rag.retriever.google_drive.dto.DocumentContentDTO;
import ai.yda.framework.rag.retriever.google_drive.dto.DocumentMetadataDTO;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentContentMapper;

public class ImageDocumentProcessor implements DocumentProcessor {

    public static final String IMAGE_DESCRIPTION_TEMPLATE =
            """
                    Provide the key information from the image, without fabricating data and unnecessary details.

                    Description:""";

    private final ChatModel chatModel;
    private final DocumentContentMapper documentContentMapper;

    public ImageDocumentProcessor(
            final @NonNull ChatModel chatModel, final @NonNull DocumentContentMapper documentContentMapper) {
        this.chatModel = chatModel;
        this.documentContentMapper = documentContentMapper;
    }

    @Override
    public List<DocumentContentDTO> processDocument(
            final InputStream inputStream, final DocumentMetadataDTO metadataDTO) {
        var imageDescriptionMsg = new UserMessage(
                IMAGE_DESCRIPTION_TEMPLATE,
                new Media(MimeType.valueOf(metadataDTO.getMimeType()), new InputStreamResource(inputStream)));
        var imageContent = this.chatModel.call(imageDescriptionMsg);
        return List.of(documentContentMapper.toDTO(imageContent, metadataDTO.getDocumentId()));
    }
}
