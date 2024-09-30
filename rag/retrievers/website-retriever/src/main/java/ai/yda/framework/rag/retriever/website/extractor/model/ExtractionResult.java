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
package ai.yda.framework.rag.retriever.website.extractor.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Represents the result of a web extraction process, containing the URL and the extracted content.
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
@Getter
@SuperBuilder(toBuilder = true)
public class ExtractionResult {

    /**
     * The URL of the extracting web source.
     */
    private final String url;

    /**
     * The extracted content.
     */
    private final String content;

    /**
     * Constructs an {@link ExtractionResult} with the specified URL and content.
     *
     * @param url     the URL of the extracting web source.
     * @param content the content extracted from the web source.
     */
    @JsonCreator
    public ExtractionResult(@JsonProperty("url") final String url, @JsonProperty("content") final String content) {
        this.url = url;
        this.content = content;
    }
}
