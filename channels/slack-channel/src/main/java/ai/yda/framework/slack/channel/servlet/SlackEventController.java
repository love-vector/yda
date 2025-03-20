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
package ai.yda.framework.slack.channel.servlet;

import jakarta.servlet.annotation.WebServlet;

import com.slack.api.bolt.App;
import com.slack.api.bolt.jakarta_servlet.SlackAppServlet;
import com.slack.api.model.event.MessageEvent;

import ai.yda.framework.slack.channel.channel.SlackChannel;

@WebServlet(SlackEventController.URL_PATTERN)
public class SlackEventController extends SlackAppServlet {
    public static final String URL_PATTERN = "/slack/events";

    public SlackEventController(final App app, final SlackChannel slackChannel) {
        super(app);
        app.event(MessageEvent.class, (payload, eventContext) -> {
            var event = payload.getEvent();
            slackChannel.sendMessage(event.getChannel(), event.getThreadTs(), event.getClientMsgId(), event.getText());
            return eventContext.ack();
        });
    }
}
