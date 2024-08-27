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

import java.util.Optional;

/**
 * Defines methods for storing and retrieving data associated with a Session using a key-value store.
 *
 * @author Dmitry Marchuk
 * @author Nikita Litvinov
 * @see ReactiveSessionProvider
 * @see ThreadLocalSessionProvider
 * @since 0.1.0
 */
public interface SessionProvider {

    /**
     * Stores a value in the Session associated with the specified key.
     *
     * @param key   the key under which the value is to be stored.
     * @param value the value to be stored in the Session.
     */
    void put(String key, Object value);

    /**
     * Retrieves the value associated with the specified key from the Session.
     *
     * @param key the key whose associated value is to be retrieved.
     * @return an {@link Optional} containing the value associated with the key, or an empty {@link Optional} if the key
     * does not exist in the Session
     */
    Optional<Object> get(String key);
}
