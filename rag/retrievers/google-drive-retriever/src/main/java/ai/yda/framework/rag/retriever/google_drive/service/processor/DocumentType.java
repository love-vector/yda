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

import java.util.List;
import java.util.stream.Stream;

import lombok.Getter;

import ai.yda.framework.rag.retriever.google_drive.exception.UnsupportedExtensionException;

@Getter
public enum DocumentType {
    EXCEL("xls", "xlsx"),
    PDF("pdf"),
    WORD("doc", "docx", "odt"),
    POWERPOINT("ppt", "pptx"),
    HTML("html"),
    PNG("png"),
    JPEG("jpeg", "jpg");

    private final List<String> extensions;

    DocumentType(final String... extensions) {
        this.extensions = List.of(extensions);
    }

    public static DocumentType fromExtension(final String extension) {
        return Stream.of(values())
                .filter(type -> type.getExtensions().contains(extension.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedExtensionException(extension));
    }
}
