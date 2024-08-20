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

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import ai.yda.framework.channel.shared.TokenAuthentication;
import ai.yda.framework.session.core.ReactiveSessionProvider;

@Component
@RequiredArgsConstructor
public class RestReactiveSessionProvider implements ReactiveSessionProvider {

    @Override
    public Mono<Void> put(final String key, final Object value) {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            if (securityContext.getAuthentication() instanceof TokenAuthentication tokenAuthentication) {
                tokenAuthentication.getAttributes().put(key, value);
            }
            return Mono.empty();
        });
    }

    @Override
    public Mono<Object> get(final String key) {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            if (securityContext.getAuthentication() instanceof TokenAuthentication tokenAuthentication) {
                var attribute = tokenAuthentication.getAttributes().get(key);
                if (attribute != null) {
                    return Mono.just(attribute);
                }
            }
            return Mono.empty();
        });
    }
}
