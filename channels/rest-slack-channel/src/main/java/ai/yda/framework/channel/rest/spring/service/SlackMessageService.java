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
package ai.yda.framework.channel.rest.spring.service;

import java.io.IOException;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import ai.yda.framework.channel.rest.spring.RestSlackProperties;

@Slf4j
public class SlackMessageService {

    private final Slack slack;
    private final RestSlackProperties restSlackProperties;

    @Autowired
    public SlackMessageService(Slack slack, RestSlackProperties restSlackProperties) {
        this.slack = slack;
        this.restSlackProperties = restSlackProperties;
    }

    @Async
    public void sendMessage(String channel, String message, String threadTs) {
        try {
            var requestBuilder = ChatPostMessageRequest.builder()
                    .channel(channel)
                    .text(message)
                    .threadTs(threadTs)
                    .build();
            var response = slack.methods(restSlackProperties.getSlackBotToken()).chatPostMessage(requestBuilder);

            if (!response.isOk()) {
                log.error("Error sending message to Slack: {}", response.getError());
            } else {
                log.info("Message successfully sent to channel {}: {}", channel, message);
            }
        } catch (SlackApiException | IOException e) {
            log.error("Error when calling Slack API", e);
        }
    }
}
