/*
 * YDA - Open-Source Java AI Assistant
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

public class ThreadLocalSessionProvider implements SessionProvider {

    private final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    @Override
    public void put(final String key, final Object value) {
        var threadLocalMap = threadLocal.get();
        if (threadLocalMap == null) {
            threadLocalMap = new HashMap<>();
            threadLocal.set(threadLocalMap);
        }
        threadLocalMap.put(key, value);
    }

    @Override
    public Optional<Object> get(final String key) {
        var threadLocalMap = threadLocal.get();
        return threadLocalMap == null ? Optional.empty() : Optional.ofNullable(threadLocalMap.get(key));
    }
}
