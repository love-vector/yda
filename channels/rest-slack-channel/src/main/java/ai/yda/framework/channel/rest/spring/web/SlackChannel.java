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
package ai.yda.framework.channel.rest.spring.web;

import java.io.IOException;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.rag.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import ai.yda.framework.channel.core.Channel;
import ai.yda.framework.channel.rest.spring.SlackProperties;
import ai.yda.framework.core.assistant.Assistant;
import ai.yda.framework.rag.core.model.RagResponse;

@Slf4j
@Service
@EnableAsync
public class SlackChannel extends Channel<Query, RagResponse> {

    private final Slack slack;
    private final SlackProperties slackProperties;

    public SlackChannel(
            final Assistant<Query, RagResponse> assistant, final Slack slack, final SlackProperties slackProperties) {
        super(assistant);
        this.slack = slack;
        this.slackProperties = slackProperties;
    }

    @Async
    public void sendMessage(String channel, String threadTs, String message) {
        try {
            var botMessage = super.processRequest(new Query(message)).getResult();
            var slackResponse = slack.methods(slackProperties.getSlackBotToken())
                    .chatPostMessage(ChatPostMessageRequest.builder()
                            .channel(channel)
                            .text(botMessage)
                            .threadTs(threadTs)
                            .build());

            if (slackResponse.isOk()) {
                log.debug("Message successfully sent to channel {}: {}", channel, botMessage);
            } else {
                log.error("Error sending message to Slack: {}", slackResponse.getError());
            }
        } catch (SlackApiException | IOException e) {
            log.error("An error occurred while calling the Slack API: {}", e.getMessage());
        }
    }
}
