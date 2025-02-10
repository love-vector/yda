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

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.rag.generator.assistant.openai.OpenAiAssistantGenerator;
import ai.yda.framework.rag.generator.assistant.openai.service.AzureOpenAiAssistantService;
import ai.yda.framework.session.core.SessionProvider;

/**
 * Autoconfiguration class for setting up an {@link OpenAiAssistantGenerator} bean and related components for OpenAI
 * integration.
 *
 * @author Iryna Kopchak
 * @author Dmitry Marchuk
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@AutoConfiguration
@EnableConfigurationProperties(OpenAiAssistantGeneratorProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class OpenAiAssistantGeneratorAutoConfiguration {

    /**
     * Default constructor for {@link OpenAiAssistantGeneratorAutoConfiguration}.
     */
    public OpenAiAssistantGeneratorAutoConfiguration() {}

    /**
     * Creates and configures an {@link OpenAiAssistantGenerator} bean.
     *
     * @param assistantService             the {@link AzureOpenAiAssistantService} used for interacting with OpenAI.
     * @param sessionProvider              the {@link SessionProvider} responsible for managing User Sessions.
     * @param assistantGeneratorProperties the properties for configuring the Assistant Generator.
     * @return a configured {@link OpenAiAssistantGenerator} bean.
     */
    @Bean
    public OpenAiAssistantGenerator openAiAssistantGenerator(
            final AzureOpenAiAssistantService assistantService,
            final SessionProvider sessionProvider,
            final OpenAiAssistantGeneratorProperties assistantGeneratorProperties) {
        return new OpenAiAssistantGenerator(
                assistantService, sessionProvider, assistantGeneratorProperties.getAssistantId());
    }
}
