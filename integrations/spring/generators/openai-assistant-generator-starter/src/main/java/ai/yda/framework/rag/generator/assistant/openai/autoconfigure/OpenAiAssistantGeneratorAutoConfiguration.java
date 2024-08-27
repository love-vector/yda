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
package ai.yda.framework.rag.generator.assistant.openai.autoconfigure;

import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.generator.assistant.openai.OpenAiAssistantGenerator;
import ai.yda.framework.session.core.SessionProvider;

/**
 * Autoconfiguration class for setting up an {@link OpenAiAssistantGenerator} bean. This class automatically configures
 * the necessary components for integrating with the OpenAI API to create an assistant generator. The configuration is
 * based on properties defined in the external configuration files (e.g., application.properties or application.yml)
 * under {@link OpenAiConnectionProperties#CONFIG_PREFIX} and {@link OpenAiAssistantGeneratorProperties#CONFIG_PREFIX}
 * namespaces.
 *
 * @author Iryna Kopchak
 * @author Dmitry Marchuk
 * @see OpenAiAssistantGenerator
 * @see OpenAiAssistantGeneratorProperties
 * @see OpenAiConnectionProperties
 * @see SessionProvider
 * @since 0.1.0
 */
@AutoConfiguration
@EnableConfigurationProperties({OpenAiAssistantGeneratorProperties.class, OpenAiConnectionProperties.class})
public class OpenAiAssistantGeneratorAutoConfiguration {

    /**
     * Default constructor for {@link OpenAiAssistantGeneratorAutoConfiguration}.
     */
    public OpenAiAssistantGeneratorAutoConfiguration() {}

    /**
     * Defines an {@link OpenAiAssistantGenerator} bean. This bean is configured using the provided properties for the
     * Assistant Generator and the OpenAI connection. The Generator requires an API key and an Assistant ID, which are
     * retrieved from the external configuration, and a {@link SessionProvider} for managing user Sessions.
     *
     * @param assistantGeneratorProperties the properties related to the Assistant Generator, providing Assistant ID
     *                                     configuration.
     * @param openAiProperties             the properties related to the OpenAI connection, including the API key.
     * @param sessionProvider              the Session Provider responsible for managing User Sessions.
     * @return a configured {@link OpenAiAssistantGenerator} bean ready for use in the application.
     */
    @Bean
    public OpenAiAssistantGenerator openAiGenerator(
            final OpenAiAssistantGeneratorProperties assistantGeneratorProperties,
            final OpenAiConnectionProperties openAiProperties,
            final SessionProvider sessionProvider) {
        return new OpenAiAssistantGenerator(
                openAiProperties.getApiKey(), assistantGeneratorProperties.getAssistantId(), sessionProvider);
    }
}
