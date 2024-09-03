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
package ai.yda.framework.channel.rest.spring.streaming.session;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;

import ai.yda.framework.session.core.ReactiveSessionProvider;

/**
 * Provides methods for storing and retrieving data associated with a Session using a key-value store  within a REST
 * context in a reactive manner.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@Component
public class RestReactiveSessionProvider implements ReactiveSessionProvider {

    /**
     * Default constructor for {@link RestReactiveSessionProvider}.
     */
    public RestReactiveSessionProvider() {}

    /**
     * Stores an object in the Session with the specified key.
     *
     * @param key   the key with which the object is to be associated.
     * @param value the object to be stored in the Session.
     */
    @Override
    public Mono<Void> put(final String key, final Object value) {
        return Mono.deferContextual(contextView -> {
            var session = contextView.getOrEmpty(WebSession.class);
            session.ifPresent(s -> ((WebSession) s).getAttributes().put(key, value));
            return Mono.empty();
        });
    }

    /**
     * Retrieves an object from the Session associated with the specified key.
     *
     * @param key the key whose associated value is to be retrieved.
     * @return an {@link Mono<Object>} containing the value associated with the key, or an empty {@link Mono} if the key
     * does not exist in the Session.
     */
    @Override
    public Mono<Object> get(final String key) {
        return Mono.deferContextual(contextView -> {
            var session = contextView.getOrEmpty(WebSession.class);
            return session.map(s -> {
                        var attributes = ((WebSession) s).getAttributes();
                        return attributes.containsKey(key) ? Mono.just(attributes.get(key)) : Mono.empty();
                    })
                    .orElseGet(Mono::empty);
        });
    }
}
