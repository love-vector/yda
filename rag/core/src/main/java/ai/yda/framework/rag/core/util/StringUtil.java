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
package ai.yda.framework.rag.core.util;

import java.util.ArrayList;
import java.util.List;

public final class StringUtil {

    public static final String POINT = ".";

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
