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

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.rag.Query;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.yda.framework.channel.core.Channel;
import ai.yda.framework.channel.rest.spring.SimpleQueryProcessorTest;
import ai.yda.framework.channel.rest.spring.SlackProperties;
import ai.yda.framework.core.assistant.Assistant;
import ai.yda.framework.rag.core.model.RagResponse;

@Slf4j
@RestController
@RequestMapping("/slack/events")
public class SlackChannel extends Channel<String, RagResponse> {
    private static final String URL_VERIFICATION_EVENT_TYPE = "url_verification";
    private static final String MESSAGE_EVENT_TYPE = "event_callback";

    //    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Slack slack;
    private final SlackProperties slackProperties;
    //    private final ChannelThreadContext channelThreadContext;
    //    private final SessionStore sessionStore;

    protected SlackChannel(
            Assistant<Query, RagResponse> assistant,
            //            QueryProcessor<String> queryProcessor,
            Slack slack,
            SlackProperties slackProperties,
            //            ChannelThreadContext channelThreadContext,
            //            SessionStore sessionStore,
            SimpleQueryProcessorTest simpleQueryProcessorTEst) {
        super(assistant, simpleQueryProcessorTEst);
        //        super(assistant, queryProcessor);
        this.slack = slack;
        this.slackProperties = slackProperties;
        //        this.channelThreadContext = channelThreadContext;
        //        this.sessionStore = sessionStore;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleSlackEvent(@RequestBody Map<String, Object> event) {
        var eventType = (String) event.get("type");
        if (URL_VERIFICATION_EVENT_TYPE.equals(eventType)) {
            return ResponseEntity.ok(Map.of("challenge", event.get("challenge")));
        } else if (MESSAGE_EVENT_TYPE.equals(eventType)) {
            var eventData = (Map<String, String>) event.get("event");
            if (eventData != null && eventData.get("bot_id") == null) {
                var channel = eventData.get("channel");
                var threadTs = eventData.get("thread_ts");
                var text = eventData.get("text");

                System.out.println("TEST");
                //                sendResponse(channel, threadTs, text);

                CompletableFuture.runAsync(() -> sendResponse(channel, threadTs, text));
            }
        }
        return ResponseEntity.ok().build();
    }

    public void sendResponse(String channel, String threadTs, String text) {
        try {
            System.out.println("WTF");
            var response = super.processRequest(text);

            System.out.println("RESPONSE: " + response);
            var slackResponse = slack.methods(slackProperties.getSlackBotToken())
                    .chatPostMessage(ChatPostMessageRequest.builder()
                            .channel(channel)
                            .text(response.getResult())
                            //                            .text(ragResponse.getResult())
                            .threadTs(threadTs)
                            .build());

            if (slackResponse.isOk()) {
                if (log.isDebugEnabled()) {
                    log.debug("Message successfully sent to channel {}: {}", channel, response.getResult());
                }
            } else {
                log.error("Error sending message to Slack: {}", slackResponse.getError());
            }
        } catch (SlackApiException | IOException e) {
            log.error("An error occurred while calling the Slack API: {}", e.getMessage());
        }
    }
}
