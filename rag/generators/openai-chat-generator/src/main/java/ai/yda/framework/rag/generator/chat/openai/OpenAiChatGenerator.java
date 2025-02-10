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
package ai.yda.framework.rag.generator.chat.openai;

import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.rag.Query;

/**
 * Generates Responses using an OpenAI Chat Model. This class is designed to interact with a Chat Model to process User
 * Requests and generate Responses based on the provided Context.
 * <p>
 * The class relies on the {@link OpenAiChatModel} for interacting with the Chat service, which processes the User's
 * Requests and generates a Responses.
 * </p>
 *
 * @author Iryna Kopchak
 * @author Nikita Litvinov
 * @see OpenAiChatModel
 * @since 0.1.0
 */
@Slf4j
public class OpenAiChatGenerator implements Generator<Query, RagResponse> {

    private final OpenAiChatModel chatModel;

    /**
     * Constructs a new {@link OpenAiChatGenerator} instance with the specified Chat Model.
     *
     * @param chatModel the {@link OpenAiChatModel} instance that defines the configuration and behavior of the Chat
     *                  Model. This Model is used by the {@link OpenAiChatGenerator} to generate and manage Chat
     *                  interactions.
     */
    public OpenAiChatGenerator(final OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Generates a Response for a given Request using the OpenAI Chat Model.
     *
     * @param query the {@link Query} object containing the query from the User.
     * @return a {@link RagResponse} containing the Content of the Chat Model's Response.
     */
    @Override
    public RagResponse generate(final Query query) {
        var prompt = new PromptTemplate(query.text()).create();

        if (log.isDebugEnabled()) {
            log.debug("Chat Completion Call:\nQuery: {}", query.text());
        }

        var response = chatModel.call(prompt).getResult().getOutput();
        return RagResponse.builder().result(response.getContent()).build();
    }
}
