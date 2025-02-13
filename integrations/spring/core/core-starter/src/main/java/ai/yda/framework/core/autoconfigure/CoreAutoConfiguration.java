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
package ai.yda.framework.core.autoconfigure;

import java.time.Duration;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import ai.yda.framework.core.rag.transformation.ReasoningCompressionQueryTransformer;
import ai.yda.framework.core.rag.transformation.ReasoningRewriteQueryTransformer;

@AutoConfiguration
@Slf4j
public class CoreAutoConfiguration {

    // TODO: delete this configuration as soon as Spring AI add timeout configuration
    @Bean
    public RestClient.Builder restClient() {
        log.warn(
                "Modules you are using rely on the internal YDA RestClient.Builder bean with timeout settings specifically designed to prevent timeouts when making requests to the AI. Defining your own bean may override this configuration, potentially causing unexpected timeouts or request failures. If this is intentional, mark your bean as @Primary to explicitly control the configuration.");
        return RestClient.builder()
                .requestFactory(ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(Duration.ofMinutes(5))
                        .withReadTimeout(Duration.ofMinutes(10))));
    }

    @Bean
    public RewriteQueryTransformer rewriteQueryTransformer(final ChatClient.Builder chatClientBuilder) {
        return new ReasoningRewriteQueryTransformer(chatClientBuilder, null, null);
    }

    @Bean
    public CompressionQueryTransformer compressionQueryTransformer(final ChatClient.Builder chatClientBuilder) {
        return new ReasoningCompressionQueryTransformer(chatClientBuilder, null);
    }
}
