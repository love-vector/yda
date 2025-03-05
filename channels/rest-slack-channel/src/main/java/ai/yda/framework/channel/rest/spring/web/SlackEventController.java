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

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/slack/events")
public class SlackEventController {
    private static final String URL_VERIFICATION_EVENT_TYPE = "url_verification";
    private static final String MESSAGE_EVENT_TYPE = "event_callback";

    private final SlackChannel slackChannel;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleSlackEvent(@RequestBody Map<String, Object> event) {
        var eventType = (String) event.get("type");
        if (URL_VERIFICATION_EVENT_TYPE.equals(eventType)) {
            return ResponseEntity.ok(Map.of("challenge", event.get("challenge")));
        } else if (MESSAGE_EVENT_TYPE.equals(eventType)) {
            var eventData = (Map<String, String>) event.get("event");
            if (eventData != null && eventData.get("bot_id") == null) {
                slackChannel.sendMessage(eventData.get("channel"), eventData.get("thread_ts"), eventData.get("text"));
            }
        }
        return ResponseEntity.ok().build();
    }
}
