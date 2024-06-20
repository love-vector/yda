package ai.yda.framework.rag.core.retriever.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public final class RegexUtil {

    /**
     * Finds and returns all URL matches in the input string.
     *
     * @param input the input string
     * @return a list of match results for URLs
     */
    public static List<MatchResult> urlMatches(final String input) {
        var matcher = PatternConstant.URL.matcher(input);
        var matches = new ArrayList<MatchResult>();
        while (matcher.find()) {
            matches.add(matcher.toMatchResult());
        }
        return matches;
    }

    private RegexUtil() {}

    private static class PatternConstant {

        private static final Pattern URL = Pattern.compile(
                "(https:\\/\\/www\\.|http:\\/\\/www\\.|https:\\/\\/|http:\\/\\/)?[a-zA-Z0-9]{2,}(\\.[a-zA-Z0-9]{2,})(\\.[a-zA-Z0-9]{2,})?");
    }
}
