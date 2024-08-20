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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Provides a way to store and retrieve Session data that is specific to the current thread of execution. Each thread
 * has its own separate storage.
 *
 * @author Nikita Litvinov
 * @see SessionProvider
 * @see ThreadLocal
 * @since 0.1.0
 */
public class ThreadLocalSessionProvider implements SessionProvider {

    private final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    /**
     * Default constructor for {@link ThreadLocalSessionProvider}.
     */
    public ThreadLocalSessionProvider() {
    }

    /**
     * Stores a value associated with a specific key in the current thread's Session storage. If the Session storage
     * for the current thread does not exist, a new {@link HashMap} will be created and associated with the thread.
     *
     * @param key   the key to associate with the value.
     * @param value the value to store in the Session.
     */
    @Override
    public void put(final String key, final Object value) {
        var threadLocalMap = threadLocal.get();
        if (threadLocalMap == null) {
            threadLocalMap = new HashMap<>();
            threadLocal.set(threadLocalMap);
        }
        threadLocalMap.put(key, value);
    }

    /**
     * Retrieves a value associated with the specified key from the current thread's Session storage. If the Session
     * storage for the current thread does not exist or does not contain the key, this method returns
     * an empty {@link Optional}.
     *
     * @param key the key associated with the value to retrieve.
     * @return an {@link Optional} containing the value if present, or an empty {@link Optional} if not.
     */
    @Override
    public Optional<Object> get(final String key) {
        var threadLocalMap = threadLocal.get();
        return threadLocalMap == null ? Optional.empty() : Optional.ofNullable(threadLocalMap.get(key));
    }
}
