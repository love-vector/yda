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
package ai.yda.framework.rag.retriever.website.extractor.util;

/**
 * Utility class for web-related operations.
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
public final class WebUtil {

    /**
     * Checks if the given URL refers to a sitemap.
     *
     * @param url the URL to check.
     * @return {@code true} if the URL contains "sitemap.xml", otherwise {@code false}.
     */
    public static Boolean isSitemapUrl(final String url) {
        return url.contains("sitemap.xml");
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private WebUtil() {}
}
