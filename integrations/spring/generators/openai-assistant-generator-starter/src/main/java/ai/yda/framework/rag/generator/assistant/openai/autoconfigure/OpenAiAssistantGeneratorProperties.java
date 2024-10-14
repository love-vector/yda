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

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Provides configuration properties for OpenAi Assistant Generator. These properties can be customized through the
 * application’s external configuration, such as a properties file, YAML file, or environment variables. The properties
 * include Assistant id settings.
 * <p>
 * The properties are prefixed with {@link #CONFIG_PREFIX} and can be customized by defining values under this prefix
 * in the external configuration.
 * <pre>
 * Example configuration in a YAML file:
 *
 * ai:
 *   yda:
 *     framework:
 *       rag:
 *          generator:
 *              assistant:
 *                  openai:
 *                      assistantId: your-assistant-id
 * </pre>
 *
 * @author Iryna Kopchak
 * @author Dmitry Marchuk
 * @since 0.1.0
 */
@Setter
@Getter
@ConfigurationProperties(OpenAiAssistantGeneratorProperties.CONFIG_PREFIX)
public class OpenAiAssistantGeneratorProperties {

    /**
     * The configuration prefix used to reference properties related to the Assistant Generator in application
     * configurations. This prefix is used for binding properties within the particular namespace.
     */
    public static final String CONFIG_PREFIX = "ai.yda.framework.rag.generator.assistant.openai";

    private String assistantId;

    private String contextResolverAssistantId;

    /**
     * Default constructor for {@link OpenAiAssistantGeneratorProperties}.
     */
    public OpenAiAssistantGeneratorProperties() {}
}
