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
package ai.yda.framework.rag.generator.assistant.openai.util;

import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.util.RequestTransformer;
import ai.yda.framework.rag.generator.assistant.openai.service.AzureOpenAiAssistantService;
import ai.yda.framework.session.core.SessionProvider;

/**
 * Resolves and enhances the {@link RagRequest} by adding context from conversation thread.
 *
 * @author Nikita Litvinov
 * @since 0.2.0
 */
public class ContextResolver implements RequestTransformer<RagRequest> {

    /**
     * Service used to interact with the Azure OpenAI Assistant API.
     */
    private final AzureOpenAiAssistantService assistantService;

    /**
     * The provider responsible for managing Session data.
     */
    private final SessionProvider sessionProvider;

    /**
     * The ID of the assistant used for resolving context within the request.
     */
    private final String contextResolverAssistantId;

    /**
     * Constructs a new {@link ContextResolver} instance.
     *
     * @param assistantService           the service used to interact with the Azure OpenAI Assistant API.
     * @param sessionProvider            the session provider used to manage session data.
     * @param contextResolverAssistantId the ID of the assistant used for resolving context in requests.
     */
    public ContextResolver(
            final AzureOpenAiAssistantService assistantService,
            final SessionProvider sessionProvider,
            final String contextResolverAssistantId) {
        this.assistantService = assistantService;
        this.sessionProvider = sessionProvider;
        this.contextResolverAssistantId = contextResolverAssistantId;
    }

    /**
     * Enhances the given {@link RagRequest} by incorporating additional context based on the conversation history.
     *
     * @param request the original {@link RagRequest} to be enhanced.
     * @return the updated {@link RagRequest} with refined query data, or the original request if no context is found.
     */
    @Override
    public RagRequest transformRequest(final RagRequest request) {
        return sessionProvider
                .get(OpenAiAssistantConstant.THREAD_ID_KEY)
                .map(Object::toString)
                .<RagRequest>map(threadId -> {
                    assistantService.addMessageToThread(threadId, request.getQuery());
                    var responseQuery =
                            assistantService.createRunAndWaitForResponse(threadId, contextResolverAssistantId);
                    return request.toBuilder().query(responseQuery).build();
                })
                .orElse(request);
    }
}
