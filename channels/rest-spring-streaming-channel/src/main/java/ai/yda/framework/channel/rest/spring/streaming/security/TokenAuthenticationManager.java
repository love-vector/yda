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
package ai.yda.framework.channel.rest.spring.streaming.security;

import reactor.core.publisher.Mono;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import ai.yda.framework.channel.shared.TokenAuthentication;
import ai.yda.framework.channel.shared.TokenAuthenticationException;

public class TokenAuthenticationManager implements ReactiveAuthenticationManager {

    private final Integer tokenKeyHash;

    public TokenAuthenticationManager(final String token) {
        this.tokenKeyHash = TokenAuthentication.extractKeyHash(token);
    }

    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) throws AuthenticationException {
        if (tokenKeyHash.equals(authentication.getCredentials())) {
            return Mono.just(authentication);
        }
        return Mono.error(new TokenAuthenticationException());
    }
}
