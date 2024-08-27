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
package ai.yda.framework.rag.core.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

/**
 * Provides utility methods to preprocess textual content.
 *
 * @author Iryna Kopchak
 * @author Nikita Litvinov
 * @since 0.1.0
 */
public final class ContentUtil {

    /**
     * A constant representing the period (.) character.
     */
    public static final String SENTENCE_SEPARATOR = ".";

    /**
     * Preprocesses the provided content by converting it to UTF-8, formatting it to lowercase, normalizing whitespaces,
     * removing HTML tags and splitting it into chunks of a specified maximum length.
     *
     * @param content       the textual content to be preprocessed.
     * @param maxCharacters the maximum number of characters per chunk.
     * @return a list of strings where each string is a chunk of the processed content. The list is unmodifiable.
     */
    public static List<String> preprocessAndSplitContent(final String content, final Integer maxCharacters) {
        var preprocessedContent = new String(content.getBytes(), StandardCharsets.UTF_8).toLowerCase();
        preprocessedContent = normalizeWhitespaces(preprocessedContent);
        preprocessedContent = removeHtmlTags(preprocessedContent);

        var contentChunks = new ArrayList<String>();
        for (int i = 0; i < preprocessedContent.length(); i += maxCharacters) {
            contentChunks.add(
                    preprocessedContent.substring(i, Math.min(preprocessedContent.length(), i + maxCharacters)));
        }
        return contentChunks;
    }

    /**
     * Normalizes whitespaces in the given content by replacing all sequences of whitespace characters (such as spaces,
     * tabs, and newlines) with a single space.
     *
     * @param content the textual content to be normalized.
     * @return the normalized content with sequences of whitespace replaced by a single space.
     */
    private static String normalizeWhitespaces(final String content) {
        return content.replaceAll("\\s+", " ");
    }

    /**
     * Removes all HTML tags from the content.
     *
     * @param content the input textual content from which HTML tags will be removed.
     * @return the content with all HTML tags removed.
     */
    private static String removeHtmlTags(final String content) {
        var document = Jsoup.parse(content);
        return document.text();
    }

    private ContentUtil() {
    }
}
