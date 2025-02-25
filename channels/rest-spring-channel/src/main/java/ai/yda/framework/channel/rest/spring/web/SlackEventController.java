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
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.rag.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.yda.framework.channel.rest.spring.RestSpringProperties;
import ai.yda.framework.rag.core.model.RagResponse;

@Slf4j
@RestController
@RequestMapping("/slack/events")
public class SlackEventController {

    private static final Set<String> processedEvents = Collections.synchronizedSet(new HashSet<>());
    private static final Set<String> processedMessages = Collections.synchronizedSet(new HashSet<>());

    private final RestSpringProperties restSpringProperties;
    private final RestChannel restChannel;
    private final Slack slack;

    @Autowired
    public SlackEventController(RestSpringProperties restSpringProperties, RestChannel restChannel, Slack slack) {
        this.restSpringProperties = restSpringProperties;
        this.restChannel = restChannel;
        this.slack = slack;
    }

    @PostMapping("/test")
    public ResponseEntity<String> testUtl() {
        return ResponseEntity.ok("done:\n" + restSpringProperties.getSlackBotToken());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleSlackEvent(@RequestBody Map<String, Object> payload) {
        if ("url_verification".equals(payload.get("type"))) {
            return ResponseEntity.ok(Map.of("challenge", payload.get("challenge")));
        }

        if ("event_callback".equals(payload.get("type"))) {
            var event = (Map<String, Object>) payload.get("event");
            var userMessage = (String) event.get("text");
            var channel = (String) event.get("channel");
            var eventId = (String) payload.get("event_id");
            var clientMsgId = (String) event.get("client_msg_id");
            var threadTs = (String) event.get("thread_ts");

            if (threadTs == null) {
                threadTs = String.valueOf(event.get("ts"));
            }

            if (!processedEvents.contains(eventId) && !processedMessages.contains(clientMsgId)) {
                processedEvents.add(eventId);
                processedMessages.add(clientMsgId);

                Query query = new Query(userMessage);
                RagResponse response = restChannel.processRequest(query);
                sendMessageToSlack(channel, response.getResult(), threadTs);
            } else {
                log.info("Duplicate event detected, skipping: {}", eventId);
            }
        }

        return ResponseEntity.ok().build();
    }

    private void sendMessageToSlack(String channel, String message, String threadTs) {
        try {
            ChatPostMessageRequest.ChatPostMessageRequestBuilder requestBuilder =
                    ChatPostMessageRequest.builder().channel(channel).text(message);

            if (threadTs != null) {
                requestBuilder.threadTs(threadTs);
            }

            ChatPostMessageResponse response =
                    slack.methods(restSpringProperties.getSlackBotToken()).chatPostMessage(requestBuilder.build());

            if (!response.isOk()) {
                log.error("Error sending a message to Slack: {}", response.getError());
            } else {
                log.info("Message successfully sent to channel {} (in thread {}): {}", channel, threadTs, message);
            }
        } catch (SlackApiException | IOException e) {
            log.error("Error when calling Slack API", e);
        }
    }
}
