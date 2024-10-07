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
package ai.yda.framework.rag.core.retriever;

/**
 * Provides a generic mechanism for extracting data from various sources, such as web pages, local files,
 * or external applications.
 *
 * @param <RESULT> the generic type of the result produced by the extraction process.
 * @author Nikita Litvinov
 * @since 0.2.0
 */
public interface DataExtractor<RESULT> {

    /**
     * Extracts data from the specified source.
     *
     * @param source the source from which data will be extracted. This could be a web URL, file path, or
     *               another type of external source.
     * @return the extracted data.
     */
    RESULT extract(String source);
}
