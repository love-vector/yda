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
package ai.yda.framework.rag.generator.chat.openai;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;

import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

@RequiredArgsConstructor
public class OpenAiChatGenerator implements Generator<RagRequest, RagResponse> {

    private final OpenAiChatModel chatModel;

    @Override
    public RagResponse generate(final RagRequest request, final String context) {
        var prompt = new Prompt(new UserMessage(request.getQuery()));
        var response = chatModel.call(prompt).getResult().getOutput();
        return RagResponse.builder().result(response.getContent()).build();
    }
}
