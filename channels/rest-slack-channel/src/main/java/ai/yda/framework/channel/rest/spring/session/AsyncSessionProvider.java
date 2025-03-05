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

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import ai.yda.framework.session.core.SessionProvider;

/**
 * Provides methods for storing and retrieving data associated with a session using a key-value store.
 * <p>
 * This component interacts with the thread-safe {@link java.util.concurrent.ConcurrentHashMap}
 * to manage Session attributes. It provides methods to add and retrieve objects from the Session based on a key.
 * </p>
 *
 * @author Iryna Kopchak
 * @since 0.2.0
 */
@Component
public class AsyncSessionProvider implements SessionProvider {

    private final Map<String, Object> session = new ConcurrentHashMap<>();

    /**
     * Stores an object in the Session with the specified key.
     *
     * @param key   the key with which the object is to be associated.
     * @param value the object to be stored in the Session.
     */
    @Override
    public void put(final String key, final Object value) {
        session.put(key, value);
    }

    /**
     * Retrieves an object from the Session associated with the specified key.
     *
     * @param key the key whose associated value is to be retrieved.
     * @return an {@link Optional} containing the value associated with the key, or an empty {@link Optional} if the key
     * does not exist in the Session.
     */
    @Override
    public Optional<Object> get(final String key) {
        return Optional.ofNullable(session.get(key));
    }
}
