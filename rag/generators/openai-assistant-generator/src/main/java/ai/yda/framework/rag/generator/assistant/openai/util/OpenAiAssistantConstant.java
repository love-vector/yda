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
package ai.yda.framework.rag.generator.assistant.openai.util;

/**
 * Contains constants used for OpenAI Assistant operations.
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
public final class OpenAiAssistantConstant {

    /**
     * The key used to store and retrieve the thread ID from session providers.
     */
    public static final String THREAD_ID_KEY = "threadId";

    /**
     * Private constructor to prevent instantiation.
     */
    private OpenAiAssistantConstant() {}
}
