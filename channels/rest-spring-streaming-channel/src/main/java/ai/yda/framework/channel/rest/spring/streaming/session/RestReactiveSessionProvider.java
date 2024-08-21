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

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;

import ai.yda.framework.session.core.ReactiveSessionProvider;

@Component
@RequiredArgsConstructor
public class RestReactiveSessionProvider implements ReactiveSessionProvider {

    @Override
    public Mono<Void> put(final String key, final Object value) {
        return Mono.deferContextual(contextView -> {
            var session = contextView.getOrEmpty(WebSession.class);
            session.ifPresent(s -> ((WebSession) s).getAttributes().put(key, value));
            return Mono.empty();
        });
    }

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
