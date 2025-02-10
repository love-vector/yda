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

import ai.yda.framework.rag.generator.assistant.openai.OpenAiAssistantStreamingGenerator;
import ai.yda.framework.rag.generator.assistant.openai.service.AzureOpenAiAssistantService;
import ai.yda.framework.session.core.ReactiveSessionProvider;

/**
 * Autoconfiguration class for setting up the {@link OpenAiAssistantStreamingGenerator} bean and related components for
 * OpenAI integration.
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
@AutoConfiguration
@EnableConfigurationProperties(OpenAiAssistantGeneratorProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class OpenAiAssistantStreamingGeneratorAutoConfiguration {

    /**
     * Default constructor for {@link OpenAiAssistantStreamingGeneratorAutoConfiguration}.
     */
    public OpenAiAssistantStreamingGeneratorAutoConfiguration() {}

    /**
     * Creates and configures an {@link OpenAiAssistantStreamingGenerator} bean.
     *
     * @param assistantService             the {@link AzureOpenAiAssistantService} used for interacting with OpenAI.
     * @param reactiveSessionProvider      the {@link ReactiveSessionProvider} responsible for managing User Sessions in
     *                                     a reactive manner.
     * @param assistantGeneratorProperties the properties for configuring the Assistant Generator.
     * @return a configured {@link OpenAiAssistantStreamingGenerator} bean.
     */
    @Bean
    public OpenAiAssistantStreamingGenerator openAiAssistantStreamingGenerator(
            final AzureOpenAiAssistantService assistantService,
            final ReactiveSessionProvider reactiveSessionProvider,
            final OpenAiAssistantGeneratorProperties assistantGeneratorProperties) {
        return new OpenAiAssistantStreamingGenerator(
                assistantService, reactiveSessionProvider, assistantGeneratorProperties.getAssistantId());
    }
}
