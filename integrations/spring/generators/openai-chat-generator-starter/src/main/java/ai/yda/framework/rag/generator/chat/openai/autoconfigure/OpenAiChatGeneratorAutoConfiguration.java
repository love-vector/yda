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
package ai.yda.framework.rag.generator.chat.openai.autoconfigure;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.generator.chat.openai.OpenAiChatGenerator;

/**
 * Autoconfiguration class for setting up a {@link Generator} bean that uses the OpenAI Chat model. This class
 * automatically configures a {@link Generator} for generating responses using OpenAI's Chat model. The configuration
 * relies on an {@link OpenAiChatModel} instance, which contains the necessary configurations and parameters to interact
 * with the OpenAI API.
 *
 * @author Dmitry Marchuk
 * @author Iryna Kopchak
 * @see Generator
 * @see OpenAiChatModel
 * @since 0.1.0
 */
@AutoConfiguration
public class OpenAiChatGeneratorAutoConfiguration {

    /**
     * Default constructor for {@link OpenAiChatGeneratorAutoConfiguration}.
     */
    public OpenAiChatGeneratorAutoConfiguration() {}

    /**
     * Defines a {@link Generator} bean that utilizes an {@link OpenAiChatModel} to generate Responses. This method
     * creates and returns an instance of {@link OpenAiChatGenerator}, which is a concrete implementation of
     * {@link Generator}. It uses the provided {@link OpenAiChatModel} to interact with OpenAI's Chat model and generate
     * Responses based on the input Requests.
     *
     * @param chatModel the {@link OpenAiChatModel} instance that provides configurations and parameters for interacting
     *                  with the OpenAI API.
     * @return a {@link Generator} instance that generates Responses using the OpenAI Chat model.
     */
    @Bean
    public Generator<Query> openAiGenerator(final OpenAiChatModel chatModel) {
        return new OpenAiChatGenerator(chatModel);
    }
}
