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
package ai.yda.framework.channel.google.drive.session;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import ai.yda.framework.session.core.SessionProvider;

@Component
public class RestSessionProvider implements SessionProvider {

    private final HttpSession httpSession;

    public RestSessionProvider(final HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public void put(final String key, final Object value) {
        httpSession.setAttribute(key, value);
    }

    @Override
    public Optional<Object> get(final String key) {
        return Optional.ofNullable(httpSession.getAttribute(key));
    }
}
