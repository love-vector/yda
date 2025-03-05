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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.rag.Query;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.yda.framework.channel.core.Channel;
import ai.yda.framework.channel.rest.spring.SlackProperties;
import ai.yda.framework.core.assistant.Assistant;
import ai.yda.framework.rag.core.model.RagResponse;
import ai.yda.framework.session.core.ThreadLocalSessionContext;

@Slf4j
@RestController
@Component
@RequestMapping("/slack/events")
public class SlackChannel extends Channel<Query, RagResponse> {
    private static final String URL_VERIFICATION_EVENT_TYPE = "url_verification";
    private static final String MESSAGE_EVENT_TYPE = "event_callback";

    private final Slack slack;
    private final SlackProperties slackProperties;
    private final ThreadLocalSessionContext threadLocalSessionContext;

    public SlackChannel(
            final Assistant<Query, RagResponse> assistant,
            final Slack slack,
            final SlackProperties slackProperties,
            final ThreadLocalSessionContext threadLocalSessionContext) {
        super(assistant);
        this.slack = slack;
        this.slackProperties = slackProperties;
        this.threadLocalSessionContext = threadLocalSessionContext;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleSlackEvent(@RequestBody Map<String, Object> event) {
        var eventType = (String) event.get("type");
        System.out.println(event);
        if (URL_VERIFICATION_EVENT_TYPE.equals(eventType)) {
            return ResponseEntity.ok(Map.of("challenge", event.get("challenge")));
        } else if (MESSAGE_EVENT_TYPE.equals(eventType)) {
            var eventData = (Map<String, String>) event.get("event");
            if (eventData != null && eventData.get("bot_id") == null) {

                ForkJoinPool customPool = new ForkJoinPool(1);
                CompletableFuture.runAsync(() -> {
                            try {
                                customPool
                                        .submit(() -> {
                                            threadLocalSessionContext.setSessionId(eventData.get("channel"));

                                            IntStream.range(0, 8)
                                                    .boxed()
                                                    .parallel()
                                                    .forEach(n -> {
                                                        System.out.printf(
                                                                "Parallel Consumer - %d: %s, Channel: %s\n",
                                                                n,
                                                                threadLocalSessionContext.getSessionId(),
                                                                eventData.get("channel"));
                                                    });
                                            System.out.println(
                                                    "CHANNEL ID: " + threadLocalSessionContext.getSessionId());
                                            sendMessage(
                                                    eventData.get("channel"),
                                                    eventData.get("thread_ts"),
                                                    eventData.get("text"));
                                        })
                                        .join();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .exceptionally(ex -> {
                            log.error("Exception ahaha:", ex);
                            return null;
                        });
            }
        }
        return ResponseEntity.ok().build();
    }

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
