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
import ai.yda.framework.rag.generator.assistant.openai.service.AzureOpenAiAssistantService;
import ai.yda.framework.rag.generator.assistant.openai.util.OpenAiAssistantConstant;
import ai.yda.framework.session.core.SessionProvider;

/**
 * Generates responses to the Request by sending queries to the Assistant Service. The class relies on the
 * {@link AzureOpenAiAssistantService} for communicating with the Assistant, and uses a {@code assistantId} field to
 * identify the Assistant being used.
 *
 * @author Iryna Kopchak
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@Slf4j
public class OpenAiAssistantGenerator implements Generator<RagRequest, RagResponse> {

    /**
     * Service used to interact with the Azure OpenAI Assistant API.
     */
    private final AzureOpenAiAssistantService assistantService;

    /**
     * The provider responsible for managing Session data.
     */
    private final SessionProvider sessionProvider;

    /**
     * The ID of the Assistant to be used.
     */
    private final String assistantId;

    /**
     * Constructs a new {@link OpenAiAssistantGenerator} instance.
     *
     * @param assistantService        the {@link AzureOpenAiAssistantService} instance used to interact with the
     *                                Azure OpenAI Service.
     * @param assistantId             the unique identifier for the Assistant that will be used to interact with the
     *                                Azure OpenAI Service. This ID specifies which Assistant to use when making
     *                                requests.
     * @param sessionProvider         the {@link SessionProvider} instance responsible for managing sessions
     *                                in a blocking manner, maintaining user context between interactions.
     */
    public OpenAiAssistantGenerator(
            final AzureOpenAiAssistantService assistantService,
            final SessionProvider sessionProvider,
            final String assistantId) {
        this.assistantService = assistantService;
        this.sessionProvider = sessionProvider;
        this.assistantId = assistantId;
    }

    /**
     * Generates a Response for a given Request using the OpenAI Assistant Service. This involves either retrieving an
     * existing Thread ID from the Session Provider or creating a new Thread, sending the Request query to the
     * Assistant, and obtaining the Response.
     *
     * @param request the {@link RagRequest} object containing the query from the User.
     * @param context the Context to be included in the Request to the Assistant.
     * @return a {@link RagResponse} containing the result of the Assistant's Response.
     */
    @Override
    public RagResponse generate(final RagRequest request, final String context) {
        var threadId = sessionProvider
                .get(OpenAiAssistantConstant.THREAD_ID_KEY)
                .map(Object::toString)
                .map(id -> assistantService
                        .addMessageToThread(id, request.getQuery())
                        .getThreadId())
                .orElseGet(() -> {
                    var newThreadId =
                            assistantService.createThread(request.getQuery()).getId();
                    sessionProvider.put(OpenAiAssistantConstant.THREAD_ID_KEY, newThreadId);
                    return newThreadId;
                });
        log.debug("Thread ID: {}", threadId);
        return RagResponse.builder()
                .result(assistantService.createRunAndWaitForResponse(threadId, assistantId, context))
                .build();
    }
}
