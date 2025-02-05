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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import static ai.yda.framework.rag.retriever.google_drive.util.Constant.DOCUMENT_METADATA_NAME;

public class TikaExcelDocumentReader extends TikaDocumentReader {

    private final Resource resource;
    private final AutoDetectParser parser;
    private final SplitSheetBodyContentHandler handler;
    private final Metadata metadata;
    private final ParseContext context;
    private final ExtractedTextFormatter textFormatter;

    public TikaExcelDocumentReader(
            Resource resource, SplitSheetBodyContentHandler contentHandler, ExtractedTextFormatter textFormatter) {
        super(resource, contentHandler, textFormatter);
        this.resource = resource;
        this.parser = new AutoDetectParser();
        this.handler = contentHandler;
        this.metadata = new Metadata();
        this.context = new ParseContext();
        this.textFormatter = textFormatter;
    }

    @Override
    public List<Document> get() {
        try (InputStream stream = this.resource.getInputStream()) {
            this.parser.parse(stream, this.handler, this.metadata, this.context);
            return this.handler.getSheetContents().entrySet().stream()
                    .map(entry -> toDocument(entry.getKey(), entry.getValue()))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Document toDocument(String sheetName, String docText) {
        return new Document(
                this.textFormatter.format(Objects.requireNonNullElse(docText, "")),
                Map.of(DOCUMENT_METADATA_NAME, sheetName, METADATA_SOURCE, resourceName()));
    }

    private String resourceName() {
        try {
            var resourceName = this.resource.getFilename();
            if (!StringUtils.hasText(resourceName)) {
                resourceName = this.resource.getURI().toString();
            }
            return resourceName;
        } catch (IOException e) {
            return String.format("Invalid source URI: %s", e.getMessage());
        }
    }
}
