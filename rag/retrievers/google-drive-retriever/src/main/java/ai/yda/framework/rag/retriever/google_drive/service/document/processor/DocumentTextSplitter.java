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
package ai.yda.framework.rag.retriever.google_drive.service.document.processor;

import java.util.List;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;

public class DocumentTextSplitter extends TokenTextSplitter {

    public DocumentTextSplitter(
            final int chunkSize,
            final int minChunkSizeChars,
            final int minChunkLengthToEmbed,
            final int maxNumChunks,
            final boolean keepSeparator) {
        super(chunkSize, minChunkSizeChars, minChunkLengthToEmbed, maxNumChunks, keepSeparator);
    }

    public List<String> splitDocumentIntoChunks(final String documentContent) {
        return splitText(documentContent);
    }
}
