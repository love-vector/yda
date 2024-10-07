package ai.yda.framework.rag.core.util;

import java.nio.charset.StandardCharsets;

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
     * @param content the textual content to be preprocessed.
     * @return a list of strings where each string is a chunk of the processed content. The list is unmodifiable.
     */
    public static String preprocessAndSplitContent(final String content) {
        var preprocessedContent = new String(content.getBytes(), StandardCharsets.UTF_8).toLowerCase();
        preprocessedContent = normalizeWhitespaces(preprocessedContent);
        preprocessedContent = removeHtmlTags(preprocessedContent);
        return preprocessedContent;
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

    private ContentUtil() {}
}
