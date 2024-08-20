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

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import ai.yda.framework.channel.shared.TokenAuthentication;

@RequiredArgsConstructor
public class TokenAuthenticationConverter implements ServerAuthenticationConverter {

    private static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";

    private static final Short TOKEN_START_POSITION = 7;

    @Override
    public Mono<Authentication> convert(final ServerWebExchange exchange) {
        var authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null) {
            return Mono.empty();
        }
        var authHeader = authHeaders.getFirst().trim();
        if (!StringUtils.startsWithIgnoreCase(authHeader, AUTHENTICATION_SCHEME_BEARER)) {
            return Mono.empty();
        }
        if (authHeader.equalsIgnoreCase(AUTHENTICATION_SCHEME_BEARER)) {
            return Mono.error(new BadCredentialsException("Empty bearer authentication token"));
        }
        var token = authHeader.substring(TOKEN_START_POSITION);
        return ReactiveSecurityContextHolder.getContext().flatMap(context -> {
            var currentAuthentication = context.getAuthentication();
            return currentAuthentication == null
                    ? Mono.just(new TokenAuthentication(token))
                    : Mono.just(new TokenAuthentication(
                            token, currentAuthentication.getPrincipal(), currentAuthentication.getAuthorities()));
        });
    }
}
