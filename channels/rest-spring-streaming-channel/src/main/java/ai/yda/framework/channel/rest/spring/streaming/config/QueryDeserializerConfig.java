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
package ai.yda.framework.channel.rest.spring.streaming.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.springframework.ai.rag.Query;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ai.yda.framework.channel.shared.QueryDeserializer;

/**
 * Configuration class responsible for setting up a deserialization of {@link Query} objects.
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
@Configuration
public class QueryDeserializerConfig {

    /**
     * Registers a custom deserializer for {@link Query} using a {@link SimpleModule}.
     *
     * @param applicationContext the application context used to get required details.
     * @return the {@link Module} containing the custom deserializer for {@link Query}.
     */
    @Bean
    public Module openAiRequestModule(final ApplicationContext applicationContext) {
        var module = new SimpleModule();
        module.addDeserializer(Query.class, new QueryDeserializer(applicationContext));
        return module;
    }
}
