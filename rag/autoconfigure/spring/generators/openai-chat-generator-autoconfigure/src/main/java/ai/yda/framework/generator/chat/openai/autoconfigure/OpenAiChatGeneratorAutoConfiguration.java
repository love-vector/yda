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
package ai.yda.framework.generator.chat.openai.autoconfigure;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.generator.chat.openai.OpenAiChatGenerator;
import ai.yda.framework.rag.core.generator.Generator;

@AutoConfiguration
@EnableConfigurationProperties({OpenAiChatGeneratorProperties.class})
public class OpenAiChatGeneratorAutoConfiguration {

    @Bean
    public Generator<BaseAssistantRequest, AssistantMessage> openAiGenerator(final OpenAiChatModel chatModel) {
        return new OpenAiChatGenerator(chatModel);
    }
}
