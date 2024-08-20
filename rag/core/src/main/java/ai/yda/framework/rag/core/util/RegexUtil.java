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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Provides utility method for working with regular expressions. It is primarily focused on finding and extracting URL
 * patterns from input strings.
 *
 * @author Nikita Litvinov
 * @see ContentUtil
 * @see StringUtil
 * @since 0.1.0
 */
public final class RegexUtil {

    /**
     * Finds and returns all URL matches in the input string.
     *
     * @param input the input string.
     * @return a list of match results for URLs.
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

    /**
     * The PatternConstant class stores precompiled regular expression for URL matching.
     */
    private static final class PatternConstant {

        private static final Pattern URL =
                Pattern.compile("(https://www\\.|http://www\\.|https://|http://)?[a-zA-Z0-9]{2,}(\\.[a-zA-Z0-9]{2,})"
                        + "(\\.[a-zA-Z0-9]{2,})?");
    }
}
