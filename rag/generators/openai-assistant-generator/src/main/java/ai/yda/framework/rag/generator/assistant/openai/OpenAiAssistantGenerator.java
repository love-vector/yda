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
package ai.yda.framework.rag.generator.assistant.openai;

import lombok.extern.slf4j.Slf4j;

import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.rag.generator.shared.AzureOpenAiAssistantService;
import ai.yda.framework.session.core.SessionProvider;

@Slf4j
public class OpenAiAssistantGenerator implements Generator<RagRequest, RagResponse> {

    private static final String THREAD_ID_KEY = "threadId";

    private final AzureOpenAiAssistantService assistantService;
    private final String assistantId;
    private final SessionProvider sessionProvider;

    public OpenAiAssistantGenerator(
            final String apiKey, final String assistantId, final SessionProvider sessionProvider) {
        this.assistantService = new AzureOpenAiAssistantService(apiKey);
        this.assistantId = assistantId;
        this.sessionProvider = sessionProvider;
    }

    @Override
    public RagResponse generate(final RagRequest request, final String context) {
        var requestQuery = request.getQuery();
        var threadId = sessionProvider
                .get(THREAD_ID_KEY)
                .map(Object::toString)
                .map(id -> {
                    assistantService.addMessageToThread(id, requestQuery);
                    return id;
                })
                .orElseGet(() -> {
                    var newThreadId =
                            assistantService.createThread(requestQuery).getId();
                    sessionProvider.put(THREAD_ID_KEY, newThreadId);
                    return newThreadId;
                });
        log.debug("Thread ID: {}", threadId);
        return RagResponse.builder()
                .result(assistantService.createRunAndWaitForResponse(threadId, assistantId, context))
                .build();
    }
}
