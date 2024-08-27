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

import ai.yda.framework.channel.shared.TokenAuthentication;
import ai.yda.framework.session.core.ReactiveSessionProvider;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Provides methods for storing and retrieving data associated with a Session using a key-value store  within a REST
 * context in a reactive manner.
 *
 * @author Nikita Litvinov
 * @see TokenAuthentication
 * @since 0.1.0
 */
@Component
public class RestReactiveSessionProvider implements ReactiveSessionProvider {

    /**
     * Default constructor for {@link RestReactiveSessionProvider}.
     */
    public RestReactiveSessionProvider() {
    }

    /**
     * Stores a Session attribute in the security context. This method retrieves the current authentication from the
     * {@link SecurityContextHolder}. If the authentication is an instance of {@link TokenAuthentication}, the Session
     * attribute is added to the attributes map of the token.
     *
     * @param key   the key under which the value is to be stored.
     * @param value the value to be stored in the Session.
     * @return a {@link Mono} that completes when the value is successfully stored.
     */
    @Override
    public Mono<Void> put(final String key, final Object value) {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            if (securityContext.getAuthentication() instanceof TokenAuthentication tokenAuthentication) {
                tokenAuthentication.getAttributes().put(key, value);
            }
            return Mono.empty();
        });
    }

    /**
     * Retrieves a Session attribute from the security context. This method retrieves the current authentication from
     * the {@link SecurityContextHolder}. If the authentication is an instance of {@link TokenAuthentication}, the
     * Session attribute corresponding to the given key is returned as an {@link Optional}.
     *
     * @param key the key whose associated value is to be retrieved.
     * @return a {@link Mono} that emits the value associated with the key, or completes without emitting a value
     * if the key does not exist in the Session.
     */
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
