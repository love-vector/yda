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
package ai.yda.framework.session.core;

import reactor.core.publisher.Mono;

/**
 * Defines methods for storing and retrieving data associated with a reactive session using a key-value store.
 *
 * @author Nikita Litvinov
 * @see SessionProvider
 * @since 0.1.0
 */
public interface ReactiveSessionProvider {

    /**
     * Stores a value in the session associated with the specified key.
     *
     * @param key   the key under which the value is to be stored.
     * @param value the value to be stored in the session.
     * @return a {@link Mono} that completes when the value is successfully stored.
     */
    Mono<Void> put(String key, Object value);

    /**
     * Retrieves the value associated with the specified key from the session.
     *
     * @param key the key whose associated value is to be retrieved.
     * @return a {@link Mono} that emits the value associated with the key, or completes without emitting a value
     * if the key does not exist in the session.
     */
    Mono<Object> get(String key);
}
