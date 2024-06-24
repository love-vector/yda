/*
 * Copyright 2023 - 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.yda.framework.channel.http.spring.autoconfigure;

import java.util.HashMap;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.channel.http.factory.HttpNettyChannelFactory;
import ai.yda.framework.core.channel.Channel;

import static ai.yda.framework.channel.http.config.HttpChannelConfig.*;

@AutoConfiguration
@EnableConfigurationProperties({ChannelHttpProperties.class})
public class ChannelHttpAutoConfiguration {

    @Bean
    public Channel<BaseAssistantRequest, BaseAssistantResponse> channelFactory(ChannelHttpProperties properties) {
        var channelFactory = new HttpNettyChannelFactory();

        var configuration = channelFactory.buildConfiguration(
                new HashMap<>() {
                    {
                        put(METHOD, properties.getMethod());
                        put(URI, properties.getUri());
                        put(PORT, properties.getPort());
                    }
                },
                BaseAssistantRequest.class,
                BaseAssistantResponse.class);

        return channelFactory.createChannel(configuration);
    }
}
