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
package ai.yda.framework.slack.channel.deserializer;

import java.util.Collections;
import java.util.List;

import com.slack.api.model.Message;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.rag.Query;
import org.springframework.stereotype.Component;

import ai.yda.framework.core.assistant.query.QueryProcessor;

@Component
public class SlackQueryProcessor implements QueryProcessor<String, List<Message>> {

    @Override
    public Query processQuery(String query, List<Message> history) {
        var messages = history.parallelStream()
                .map(message -> message.getBotId() != null
                        ? (org.springframework.ai.chat.messages.Message) new AssistantMessage(message.getText())
                        : new UserMessage(message.getText()))
                .toList();
        return new Query(query, messages, Collections.emptyMap());
    }
}
