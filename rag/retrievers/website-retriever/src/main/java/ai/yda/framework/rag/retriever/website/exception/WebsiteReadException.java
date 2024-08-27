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
package ai.yda.framework.rag.retriever.website.exception;

/**
 * Thrown to indicate that website processing operation has failed.
 *
 * @author Bogdan Synenko
 * @since 0.1.0
 */
public class WebsiteReadException extends RuntimeException {

    /**
     * Constructs a new {@link  WebsiteReadException} instance with the specified cause.
     * This constructor initializes the exception with a predefined message "Failed to process site".
     *
     * @param cause the cause of the exception, which can be retrieved later using {@link Throwable#getCause()}.
     */
    public WebsiteReadException(final Throwable cause) {
        super("Failed to process site", cause);
    }
}
