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

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ai.yda.framework.channel.shared.TokenAuthentication;
import ai.yda.framework.session.core.SessionProvider;

@Component
@RequiredArgsConstructor
public class RestSessionProvider implements SessionProvider {

    @Override
    public void put(final String key, final Object value) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof TokenAuthentication tokenAuthentication) {
            tokenAuthentication.getAttributes().put(key, value);
        }
    }

    @Override
    public Optional<Object> get(final String key) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof TokenAuthentication tokenAuthentication) {
            return Optional.ofNullable(tokenAuthentication.getAttributes().get(key));
        }
        return Optional.empty();
    }
}
