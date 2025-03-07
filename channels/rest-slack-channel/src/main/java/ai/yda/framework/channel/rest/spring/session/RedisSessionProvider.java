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

import ai.yda.framework.core.session.UserThreadContext;
import ai.yda.framework.core.session.provider.SessionProvider;

public class RedisSessionProvider implements SessionProvider {

    //    private final RedisClient redisClient;
    private final UserThreadContext userThreadContext;

    public RedisSessionProvider(/*RedisClient redisClient,*/ UserThreadContext userThreadContext) {
        //        this.redisClient = redisClient;
        this.userThreadContext = userThreadContext;
    }

    @Override
    public void put(String key, Object value) {}

    @Override
    public Optional<Object> get(String key) {

        //        var sessionKey = userThreadContext.getSessionKey();
        //
        //        redisClient.get(String.format("{'user_id': '%s', 'thread_id': '%s'}"),
        // userThreadContext.getSessionKey());

        return Optional.empty();
    }
}
