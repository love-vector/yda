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
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.rag.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import ai.yda.framework.channel.core.Channel;
import ai.yda.framework.channel.rest.spring.RestSlackProperties;
import ai.yda.framework.channel.rest.spring.session.RestSessionProvider;
import ai.yda.framework.core.assistant.Assistant;
import ai.yda.framework.rag.core.model.RagResponse;

@Slf4j
@RestController
@RequestMapping("/slack/events")
public class SlackEventController extends Channel<Query, RagResponse> {

    private final Slack slack;
    private final RestSlackProperties restSlackProperties;
    private final RestSessionProvider sessionProvider;

    @Autowired
    public SlackEventController(
            final Assistant<Query, RagResponse> assistant,
            Slack slack,
            RestSlackProperties restSlackProperties,
            RestSessionProvider sessionProvider) {
        super(assistant);
        this.slack = slack;
        this.restSlackProperties = restSlackProperties;
        this.sessionProvider = sessionProvider;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleSlackEvent(
            @RequestBody Map<String, Object> payload, @RequestHeader("X-Slack-Request-Timestamp") String timestamp) {

        long requestTime = Long.parseLong(timestamp);
        long currentTime = Instant.now().getEpochSecond();

        var payloadType = (String) payload.get("type");
        if ("url_verification".equals(payloadType)) {
            return ResponseEntity.ok(Map.of("challenge", payload.get("challenge")));
        }

        log.info("Processing message:");

        if (Math.abs(currentTime - requestTime) > 60) {
            log.warn("Received an outdated Slack event ({} seconds old), ignoring.", currentTime - requestTime);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ignored outdated request");
        }

        var event = (Map<String, Object>) payload.get("event");
        String eventId = (String) payload.get("event_id");
        String userMessage = event != null ? (String) event.get("text") : null;
        Object botId = event != null ? event.get("bot_id") : null;
        String channel = (String) event.get("channel");
        String threadTs = (String) event.get("thread_ts");

        log.info("Processing event: {}", eventId);
        if (userMessage != null && !userMessage.isEmpty() && botId == null) {
            sendMessage(channel, threadTs, userMessage);
        } else {
            log.warn("Ignoring event {}: userMessage is null or empty.", eventId);
        }

        log.info("Response sanded: {}");
        return ResponseEntity.ok().build();
    }

    // threadId
    @Async
    public void sendMessage(String channel, String threadTs, String message) {
        CompletableFuture.runAsync(() -> {
            try {
                var responseTest = processRequest(new Query(message)).getResult();
                var requestBuilder = ChatPostMessageRequest.builder()
                        .channel(channel)
                        .text("Привет как дела ?")
                        .threadTs(threadTs)
                        .build();
                var response =
                        slack.methods(restSlackProperties.getSlackBotToken()).chatPostMessage(requestBuilder);

                if (!response.isOk()) {
                    log.error("Error sending message to Slack: {}", response.getError());
                } else {
                    log.info("Message successfully sent to channel {}: {}", channel, responseTest);
                }
            } catch (SlackApiException | IOException e) {
                log.error("Error when calling Slack API", e);
            }
        });
    }
}
