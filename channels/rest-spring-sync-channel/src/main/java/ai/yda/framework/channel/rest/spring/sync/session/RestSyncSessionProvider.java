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
package ai.yda.framework.channel.rest.spring.sync.session;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import ai.yda.framework.session.core.SessionProvider;

@Component
@RequiredArgsConstructor
public class RestSyncSessionProvider implements SessionProvider {

    @Override
    public void put(final String key, final Object value) {
        RequestContextHolder.currentRequestAttributes().setAttribute(key, value, RequestAttributes.SCOPE_SESSION);
    }

    @Override
    public Optional<Object> get(final String key) {
        return Optional.ofNullable(
                RequestContextHolder.currentRequestAttributes().getAttribute(key, RequestAttributes.SCOPE_SESSION));
    }
}
