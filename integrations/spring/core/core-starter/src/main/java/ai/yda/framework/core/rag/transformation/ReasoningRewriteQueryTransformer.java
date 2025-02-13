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

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Slf4j
public class ReasoningRewriteQueryTransformer extends RewriteQueryTransformer {

    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate(
            """
            Given a user query, rewrite it to provide better results when querying a {target}.
            Remove any irrelevant information, and ensure the query is concise and specific.

            Original query:
            {query}

            Rewritten query:
            """);

    private static final String DEFAULT_TARGET = "google drive documents";

    private final ChatClient chatClient;

    private final PromptTemplate promptTemplate;

    private final String targetSearchSystem;

    public ReasoningRewriteQueryTransformer(
            ChatClient.Builder chatClientBuilder, PromptTemplate promptTemplate, String targetSearchSystem) {
        super(chatClientBuilder, promptTemplate, targetSearchSystem);
        this.chatClient = chatClientBuilder.build();
        this.promptTemplate = promptTemplate != null ? promptTemplate : DEFAULT_PROMPT_TEMPLATE;
        this.targetSearchSystem = targetSearchSystem != null ? targetSearchSystem : DEFAULT_TARGET;
    }

    @Override
    public Query transform(Query query) {
        Assert.notNull(query, "query cannot be null");

        log.debug("Rewriting query to optimize for querying a {}.", this.targetSearchSystem);

        var rewrittenQueryText = this.chatClient
                .prompt()
                .user(user -> user.text(this.promptTemplate.getTemplate())
                        .param("target", targetSearchSystem)
                        .param("query", query.text()))
                .options(ReasoningOpenAiChatOptions.builder()
                        .reasoningEffort("medium")
                        .build())
                .call()
                .content();

        if (!StringUtils.hasText(rewrittenQueryText)) {
            log.warn("Query rewrite result is null/empty. Returning the input query unchanged.");
            return query;
        }

        return query.mutate().text(rewrittenQueryText).build();
    }
}
