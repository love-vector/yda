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
package ai.yda.framework.channel.core;

import reactor.core.publisher.Flux;

import org.springframework.ai.rag.Query;

import ai.yda.framework.core.assistant.StreamingAssistant;
import ai.yda.framework.core.assistant.query.QueryProcessor;
import ai.yda.framework.core.assistant.query.processor.SimpleQueryProcessor;

/**
 * Provides an abstract class for implementing communication gateways to the streaming Assistant.
 *
 * @param <QUERY>  the generic type of the query from the User.
 * @param <RESPONSE> the generic type of the Response that will be generated based on the given Request.
 * @author Nikita Litvinov
 * @see Channel
 * @since 0.1.0
 */
public abstract class StreamingChannel<QUERY, HISTORY, RESPONSE> {

    private final QueryProcessor<QUERY, HISTORY> queryProcessor;

    private final StreamingAssistant<Query, RESPONSE> assistant;

    protected StreamingChannel(final StreamingAssistant<Query, RESPONSE> assistant) {
        this.assistant = assistant;
        this.queryProcessor = new SimpleQueryProcessor<>();
    }

    protected StreamingChannel(
            final StreamingAssistant<Query, RESPONSE> assistant, final QueryProcessor<QUERY, HISTORY> queryProcessor) {
        this.assistant = assistant;
        this.queryProcessor = queryProcessor;
    }

    /**
     * Processes the Request data involving the streaming Assistant.
     *
     * @param query the Request object to be processed.
     * @return a {@link Flux} stream of Response objects generated after processing the Request.
     */
    public Flux<RESPONSE> processRequest(QUERY query, HISTORY history) {
        return this.assistant.streamAssistance(this.queryProcessor.processQuery(query, history));
    }
}
