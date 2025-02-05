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
package ai.yda.framework.assistant.rag.autoconfigure;

import org.springframework.ai.rag.Query;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.core.assistant.StreamingRagAssistant;
import ai.yda.framework.rag.core.StreamingRag;

/**
 * Autoconfiguration class for setting up a {@link StreamingRagAssistant} bean in a Spring application.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class StreamingRagAssistantAutoConfiguration {

    public StreamingRagAssistantAutoConfiguration() {}

    @Bean
    public StreamingRagAssistant streamingAssistant(final StreamingRag<Query> streamingRag) {
        return new StreamingRagAssistant(streamingRag);
    }
}
