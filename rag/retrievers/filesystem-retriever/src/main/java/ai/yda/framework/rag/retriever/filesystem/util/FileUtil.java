/*
 * YDA - Open-Source Java AI Assistant
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
package ai.yda.framework.rag.retriever.filesystem.util;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;

import ai.yda.framework.rag.retriever.filesystem.exception.FileReadException;

public final class FileUtil {

    public static String readPdf(final File file) {
        try (var document = Loader.loadPDF(file)) {
            return new PDFTextStripper().getText(document);
        } catch (final IOException exception) {
            throw new FileReadException(exception);
        }
    }

    private FileUtil() {}
}
