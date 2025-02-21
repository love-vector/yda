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
package ai.yda.framework.rag.retriever.google_drive.service.document.reader;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SplitSheetBodyContentHandler extends BodyContentHandler {

    @Getter
    private final Map<String, String> sheetContents = new LinkedHashMap<>();

    private final StringBuilder currentSheetLabel = new StringBuilder();

    private boolean insideSheet = false;
    private boolean skipCellText = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("h1".equalsIgnoreCase(qName)) {
            insideSheet = true;
        } else if ("td".equalsIgnoreCase(qName)) {
            // we're not in the commentary now
            skipCellText = false;
        }

        super.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (insideSheet) {
            currentSheetLabel.append(ch, start, length);
        } else if (!skipCellText) {
            super.characters(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if ("h1".equalsIgnoreCase(qName)) {
            insideSheet = false;
        } else if ("br".equalsIgnoreCase(qName)) {
            // we're in the commentary now
            skipCellText = true;
        } else if ("table".equalsIgnoreCase(qName)) {
            sheetContents.put(
                    currentSheetLabel.toString(),
                    sheetContents.values().stream()
                            .reduce(this.toString(), (acc, toRemove) -> acc.replace(toRemove, "")));
            currentSheetLabel.setLength(0);
        }
        super.endElement(uri, localName, qName);
    }
}
