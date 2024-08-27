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
package ai.yda.framework.channel.rest.spring.session;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ai.yda.framework.channel.shared.TokenAuthentication;
import ai.yda.framework.session.core.SessionProvider;

/**
 * Provides methods for storing and retrieving data associated with a Session using a key-value store in the REST
 * context.
 *
 * @author Nikita Litvinov
 * @see TokenAuthentication
 * @since 0.1.0
 */
@Component
public class RestSessionProvider implements SessionProvider {

    /**
     * Default constructor for {@link RestSessionProvider}.
     */
    public RestSessionProvider() {
    }

    /**
     * Stores a Session attribute in the security context. This method retrieves the current authentication from the
     * {@link SecurityContextHolder}. If the authentication is an instance of {@link TokenAuthentication}, the Session
     * attribute is added to the attributes map of the token.
     *
     * @param key   the key under which the value is to be stored.
     * @param value the value to be stored in the Session.
     */
    @Override
    public void put(final String key, final Object value) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof TokenAuthentication tokenAuthentication) {
            tokenAuthentication.getAttributes().put(key, value);
        }
    }

    /**
     * Retrieves a Session attribute from the security context. This method retrieves the current authentication from
     * the {@link SecurityContextHolder}. If the authentication is an instance of {@link TokenAuthentication}, the
     * Session attribute corresponding to the given key is returned as an {@link Optional}.
     *
     * @param key the key whose associated value is to be retrieved.
     * @return an {@link Optional} containing the value associated with the key, or an empty {@link Optional} if the key
     * does not exist in the Session
     */
    @Override
    public Optional<Object> get(final String key) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof TokenAuthentication tokenAuthentication) {
            return Optional.ofNullable(tokenAuthentication.getAttributes().get(key));
        }
        return Optional.empty();
    }
}
