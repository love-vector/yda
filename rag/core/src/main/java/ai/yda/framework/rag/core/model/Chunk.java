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
package ai.yda.framework.rag.core.model;

public class Chunk {
    private final String text;
    private final int index;
    private final String documentId;

    public Chunk(String text, int index, String documentId) {
        this.text = text;
        this.index = index;
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getText() {
        return text;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "Chunk{" + "text='" + text + '\'' + ", index=" + index + ", documentId='" + documentId + '\'' + '}';
    }
}
