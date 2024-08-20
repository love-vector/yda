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
package ai.yda.framework.rag.generator.assistant.openai.streaming.autoconfigure;

import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.generator.assistant.openai.streaming.OpenAiAssistantStreamingGenerator;
import ai.yda.framework.session.core.ReactiveSessionProvider;

/**
 * Autoconfiguration class for setting up an {@link OpenAiAssistantStreamingGenerator} bean. This class automatically
 * configures the necessary components for integrating with the OpenAI API to create a streaming assistant generator.
 * The configuration is based on properties defined in the external configuration files (e.g., application.properties or
 * application.yml) under {@link OpenAiConnectionProperties#CONFIG_PREFIX} and
 * {@link OpenAiAssistantStreamingGeneratorProperties#CONFIG_PREFIX} namespaces.
 *
 * @author Nikita Litvinov
 * @see OpenAiAssistantStreamingGenerator
 * @see OpenAiAssistantStreamingGeneratorProperties
 * @see OpenAiConnectionProperties
 * @see ReactiveSessionProvider
 * @since 0.1.0
 */
@AutoConfiguration
@EnableConfigurationProperties({OpenAiAssistantStreamingGeneratorProperties.class, OpenAiConnectionProperties.class})
public class OpenAiAssistantStreamingGeneratorAutoConfiguration {

    /**
     * Default constructor for {@link OpenAiAssistantStreamingGeneratorAutoConfiguration}.
     */
    public OpenAiAssistantStreamingGeneratorAutoConfiguration() {}

    /**
     * Defines an {@link OpenAiAssistantStreamingGenerator} bean. This bean is configured using the provided properties
     * for the streaming assistant generator and the OpenAI connection. The generator requires an API key and an
     * assistant ID, which are retrieved from the external configuration, and a {@link ReactiveSessionProvider} for
     * managing user sessions.
     *
     * @param assistantGeneratorProperties the properties related to the assistant generator, providing assistant ID
     *                                     configuration.
     * @param openAiProperties             the properties related to the OpenAI connection, including the API key.
     * @param sessionProvider              the session provider responsible for managing user sessions.
     * @return a configured {@link OpenAiAssistantStreamingGenerator} bean ready for use in the application.
     */
    @Bean
    public OpenAiAssistantStreamingGenerator openAiGenerator(
            final OpenAiAssistantStreamingGeneratorProperties assistantGeneratorProperties,
            final OpenAiConnectionProperties openAiProperties,
            final ReactiveSessionProvider sessionProvider) {
        return new OpenAiAssistantStreamingGenerator(
                openAiProperties.getApiKey(), assistantGeneratorProperties.getAssistantId(), sessionProvider);
    }
}
