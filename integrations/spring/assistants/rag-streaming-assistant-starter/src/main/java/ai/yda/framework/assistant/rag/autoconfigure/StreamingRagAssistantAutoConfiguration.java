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

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.core.assistant.StreamingRagAssistant;
import ai.yda.framework.rag.core.StreamingRag;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

/**
 * Autoconfiguration class for setting up a {@link StreamingRagAssistant} bean. This class is responsible for
 * automatically configuring a {@link StreamingRagAssistant} instance within the Spring application context. The
 * {@link StreamingRagAssistant} bean is created using a provided {@link StreamingRag} instance, which is injected as a
 * dependency. The configuration is applied automatically when the application starts, thanks to the
 * {@link AutoConfiguration} annotation.
 * <p>
 * This setup allows the {@link StreamingRagAssistant} to be readily available for use in the application wherever it's
 * needed, without requiring manual bean configuration.
 * </p>
 *
 * @author Nikita Litvinov
 * @see StreamingRagAssistant
 * @see StreamingRag
 * @since 0.1.0
 */
@AutoConfiguration
public class StreamingRagAssistantAutoConfiguration {

    /**
     * Default constructor for {@link StreamingRagAssistantAutoConfiguration}.
     */
    public StreamingRagAssistantAutoConfiguration() {}

    /**
     * Creates and configures a {@link StreamingRagAssistant} bean in the Spring application context.
     *
     * @param rag the {@link StreamingRag} instance used by the {@link StreamingRagAssistant} to perform its operations.
     *            This parameter is expected to be provided by the application or other autoconfiguration.
     * @return a configured {@link StreamingRagAssistant} bean.
     */
    @Bean
    public StreamingRagAssistant streamingRagAssistant(final StreamingRag<RagRequest, RagResponse> rag) {
        return new StreamingRagAssistant(rag);
    }
}
