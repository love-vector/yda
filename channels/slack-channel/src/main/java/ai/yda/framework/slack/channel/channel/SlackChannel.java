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
import java.util.concurrent.ForkJoinPool;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.rag.Query;
import org.springframework.stereotype.Component;

import ai.yda.framework.channel.core.Channel;
import ai.yda.framework.core.assistant.Assistant;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.session.core.ThreadLocalSessionContext;
import ai.yda.framework.slack.channel.SlackProperties;

@Slf4j
@Component
public class SlackChannel extends Channel<Query, RagResponse> {

    private final Slack slack;
    private final SlackProperties properties;
    private final ThreadLocalSessionContext sessionContext;

    public SlackChannel(
            final Assistant<Query, RagResponse> assistant,
            final Slack slack,
            final SlackProperties properties,
            final ThreadLocalSessionContext sessionContext) {
        super(assistant);
        this.slack = slack;
        this.properties = properties;
        this.sessionContext = sessionContext;
    }

    public void sendMessage(final String channel, final String threadTs, final String message) {
        var concurrentExecutor = new ForkJoinPool(1);
        concurrentExecutor.submit(() -> {
            try {
                sessionContext.setSessionId(channel);
                var botMessage = super.processRequest(new Query(message)).getResult();
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
}
