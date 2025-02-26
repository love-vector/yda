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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
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

@Slf4j
@RestController
@RequestMapping("/slack/events")
public class SlackEventController {

    private static final Set<String> processedEvents = new HashSet<>();
    private static final Set<String> processedMessages = new HashSet<>();

    private final RestSpringProperties restSpringProperties;
    private final RestChannel restChannel;
    private final Slack slack;

    @Autowired
    public SlackEventController(RestSpringProperties restSpringProperties, RestChannel restChannel, Slack slack) {
        this.restSpringProperties = restSpringProperties;
        this.restChannel = restChannel;
        this.slack = slack;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleSlackEvent(@RequestBody Map<String, Object> payload) throws IOException {
        var payloadType = (String) payload.get("type");
        if (payloadType.equals("url_verification")) {
            return ResponseEntity.ok(Map.of("challenge", payload.get("challenge")));
        } else {
            return eventProcessor(payload);
        }
    }

    // Обработать сообщение
    private ResponseEntity<?> eventProcessor(Map<String, Object> payload) {
        var event = (Map<String, Object>) payload.get("event");
        var eventId = (String) payload.get("event_id");
        var userMassage = (String) event.get("text");
        var channel = (String) event.get("channel");
        var threadTs = (String) event.get("thread_ts");
        var clientMsgId = (String) event.get("client_msg_id");

        if (!processedEvents.contains(eventId) && !processedMessages.contains(clientMsgId) && clientMsgId != null) {
            processedEvents.add(eventId);
            log.info("Processing event: {}", eventId);
            var response = restChannel.processRequest(new Query(userMassage)).getResult();
            sendMessageToSlack(channel, response, threadTs);
        }

        return ResponseEntity.ok().build();
    }

    private void sendMessageToSlack(final String channel, final String message, final String thread) {
        try {
            var requestBuilder = ChatPostMessageRequest.builder()
                    .channel(channel)
                    .text(message)
                    .threadTs(thread)
                    .build();
            var response =
                    slack.methods(restSpringProperties.getSlackBotToken()).chatPostMessage(requestBuilder);

            if (!response.isOk()) {
                log.error("Error sending a message to Slack: {}", response.getError());
            } else {
                log.info("Message successfully sent to channel {} : {}", channel, message);
            }
        } catch (SlackApiException | IOException e) {
            log.error("Error when calling Slack API", e);
        }
    }
}
