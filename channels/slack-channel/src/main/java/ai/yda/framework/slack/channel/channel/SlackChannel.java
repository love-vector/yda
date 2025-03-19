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
package ai.yda.framework.slack.channel.channel;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.request.conversations.ConversationsRepliesRequest;
import com.slack.api.model.Message;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.rag.Query;
import org.springframework.stereotype.Component;

import ai.yda.framework.channel.core.Channel;
import ai.yda.framework.core.assistant.Assistant;
import ai.yda.framework.core.assistant.query.QueryProcessor;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.session.core.ThreadLocalSessionContext;
import ai.yda.framework.slack.channel.SlackProperties;

@Slf4j
@Component
public class SlackChannel extends Channel<String, List<Message>, RagResponse> {

    private final Slack slack;
    private final SlackProperties properties;
    private final ThreadLocalSessionContext sessionContext;

    public SlackChannel(
            final Assistant<Query, RagResponse> assistant,
            final QueryProcessor<String, List<Message>> queryProcessor,
            final Slack slack,
            final SlackProperties properties,
            final ThreadLocalSessionContext sessionContext) {
        super(assistant, queryProcessor);
        this.slack = slack;
        this.properties = properties;
        this.sessionContext = sessionContext;
    }

    public void sendMessage(final String channel, final String threadTs, final String message) {
        var concurrentExecutor = new ForkJoinPool(1);
        concurrentExecutor.submit(() -> {
            try {
                sessionContext.setSessionId(channel);

                var slackMessageHistory = getSlackMessageHistory(channel, threadTs);
                var botMessage =
                        super.processRequest(message, slackMessageHistory).getResult();

                var slackResponse = slack.methods(properties.getBotToken())
                        .chatPostMessage(ChatPostMessageRequest.builder()
                                .channel(channel)
                                .text(botMessage)
                                .threadTs(threadTs)
                                .build());

                if (slackResponse.isOk()) {
                    log.debug("Message successfully sent to channel {}: {}", channel, botMessage);
                } else {
                    log.error(
                            "Failed to send message to Slack channel {}. Slack API returned error: {}",
                            channel,
                            slackResponse.getError());
                }
            } catch (SlackApiException | IOException e) {
                log.error("An error occurred while calling the Slack API: {}", e.getMessage());
            }
        });
    }

    private List<Message> getSlackMessageHistory(final String channel, final String threadTs)
            throws SlackApiException, IOException {
        if (threadTs != null) {
            return slack.methods(properties.getBotToken())
                    .conversationsReplies(ConversationsRepliesRequest.builder()
                            .channel(channel)
                            .ts(threadTs)
                            .build())
                    .getMessages();
        }

        return slack.methods(properties.getBotToken())
                .conversationsHistory(
                        ConversationsHistoryRequest.builder().channel(channel).build())
                .getMessages();
    }
}
