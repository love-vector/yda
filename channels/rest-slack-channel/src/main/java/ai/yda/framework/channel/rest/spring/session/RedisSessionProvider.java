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

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ai.yda.framework.core.session.provider.SessionProvider;

@Component
@RequiredArgsConstructor
public class RedisSessionProvider implements SessionProvider {

    //    private final RedisClient redisClient;
    //    private final ChannelThreadContext channelThreadContext;
    //    private final SessionStore sessionStore;

    private final Map<String, Object> session = new ConcurrentHashMap<>();

    //    public RedisSessionProvider(/*RedisClient redisClient,*/ ChannelThreadContext channelThreadContext) {
    //        //        this.redisClient = redisClient;
    //        this.channelThreadContext = channelThreadContext;
    //    }

    @Override
    public void put(String key, Object value) {
        session.put(key, value);
        //        if (sessionStore.sessionExists(channelThreadContext.getChannelId())) {
        //            sessionStore.getSession(channelThreadContext.getChannelId()).put(key, value);
        //        }
    }

    @Override
    public Optional<Object> get(String key) {
        return Optional.of(session.get(key));
        //        return Optional.of(
        //                sessionStore.getSession(channelThreadContext.getChannelId()).get(key));

        //        var sessionKey = userThreadContext.getSessionKey();
        //
        //        redisClient.get(String.format("{'user_id': '%s', 'thread_id': '%s'}"),
        // userThreadContext.getSessionKey());

        //        return Optional.empty();
    }
}
