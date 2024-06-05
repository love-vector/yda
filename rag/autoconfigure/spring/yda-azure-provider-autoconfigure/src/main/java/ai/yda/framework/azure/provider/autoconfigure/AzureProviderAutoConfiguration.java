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
package ai.yda.framework.azure.provider.autoconfigure;

import com.azure.ai.openai.assistants.AssistantsClientBuilder;
import com.azure.core.credential.KeyCredential;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.framework.azure.provider.AzureProvider;

@AutoConfiguration
@EnableConfigurationProperties({AzureProviderProperties.class})
public class AzureProviderAutoConfiguration {

    @Bean
    public AzureProvider azureProvider(AzureProviderProperties properties) {
        var assistantClient = new AssistantsClientBuilder()
                .credential(new KeyCredential(properties.getApiKey()))
                .buildClient();
        return new AzureProvider(properties.getModel(), assistantClient);
    }
}
