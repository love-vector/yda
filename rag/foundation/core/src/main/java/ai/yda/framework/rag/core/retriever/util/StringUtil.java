package ai.yda.framework.rag.core.retriever.util;

import java.util.ArrayList;
import java.util.List;

public final class StringUtil {

    /**
     * Splits the input string into equal parts with a maximum number of characters.
     *
     * @param input         the input string
     * @param maxCharacters the maximum number of characters per part
     * @return a list of string parts
     */
    public static List<String> splitIntoEqualsParts(final String input, final Integer maxCharacters) {
        var parts = new ArrayList<String>();
        for (int i = 0; i < input.length(); i += maxCharacters) {
            parts.add(input.substring(i, Math.min(input.length(), i + maxCharacters)));
        }
        return parts;
    }

    private StringUtil() {}
}
