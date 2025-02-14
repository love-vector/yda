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
package ai.yda.framework.core.rag.transformation;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Slf4j
public class ReasoningCompressionQueryTransformer extends CompressionQueryTransformer {

    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate(
            """
			Given the following conversation history and a follow-up query, your task is to synthesize
			a concise, standalone query that incorporates the context from the history.
			Ensure the standalone query is clear, specific, and maintains the user's intent.

			Conversation history:
			{history}

			Follow-up query:
			{query}

			Standalone query:
			""");

    private final ChatClient chatClient;

    private final PromptTemplate promptTemplate;

    public ReasoningCompressionQueryTransformer(ChatClient.Builder chatClientBuilder, PromptTemplate promptTemplate) {
        super(chatClientBuilder, promptTemplate);
        this.chatClient = chatClientBuilder.build();
        this.promptTemplate = promptTemplate != null ? promptTemplate : DEFAULT_PROMPT_TEMPLATE;
    }

    @Override
    public Query transform(Query query) {
        Assert.notNull(query, "query cannot be null");

        log.debug("Compressing conversation history and follow-up query into a standalone query");

        var compressedQueryText = this.chatClient
                .prompt()
                .user(user -> user.text(this.promptTemplate.getTemplate())
                        .param("history", formatConversationHistory(query.history()))
                        .param("query", query.text()))
                .options(ReasoningOpenAiChatOptions.builder()
                        .reasoningEffort("medium")
                        .build())
                .call()
                .content();

        if (!StringUtils.hasText(compressedQueryText)) {
            log.warn("Query compression result is null/empty. Returning the input query unchanged.");
            return query;
        }

        return query.mutate().text(compressedQueryText).build();
    }

    private String formatConversationHistory(List<Message> history) {
        if (history.isEmpty()) {
            return "";
        }

        return history.stream()
                .filter(message -> message.getMessageType().equals(MessageType.USER)
                        || message.getMessageType().equals(MessageType.ASSISTANT))
                .map(message -> "%s: %s".formatted(message.getMessageType(), message.getText()))
                .collect(Collectors.joining("\n"));
    }
}
