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
package ai.yda.framework.channel.google.drive.streaming.security;

import reactor.core.publisher.Mono;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import ai.yda.framework.channel.shared.TokenAuthentication;

/**
 * Provides strategy used for converting from a {@link ServerWebExchange} to an {@link Mono<TokenAuthentication>}. Used
 * to authenticate with {@link TokenAuthenticationManager}. If the result is null, then it signals that no
 * authentication attempt should be made.
 *
 * @author Nikita Litvinov
 * @see TokenAuthentication
 * @since 0.1.0
 */
public class TokenAuthenticationConverter implements ServerAuthenticationConverter {

    /**
     * The authentication scheme used for Bearer tokens in the authorization header. This constant is used to indicate
     * that the token should be prefixed with the word "Bearer".
     */
    private static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";

    /**
     * The starting position of the token in the authorization header. This constant represents the index from which the
     * actual token starts, after the "Bearer " prefix.
     */
    private static final Short TOKEN_START_POSITION = 7;

    /**
     * Default constructor for {@link TokenAuthenticationConverter}.
     */
    public TokenAuthenticationConverter() {}

    /**
     * Converts the given {@link ServerWebExchange} to an {@link Mono<TokenAuthentication>}. If the result is null,
     * then it signals that no authentication attempt should be made.
     *
     * @param exchange the {@link ServerWebExchange} to be converted.
     * @return the {@link Mono<Authentication>} that is a result of conversion or null if there is no authentication.
     */
    @Override
    public Mono<Authentication> convert(final ServerWebExchange exchange) {
        var authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null) {
            return Mono.empty();
        }
        var authHeader = authHeaders.get(0).trim();
        if (!StringUtils.startsWithIgnoreCase(authHeader, AUTHENTICATION_SCHEME_BEARER)) {
            return Mono.empty();
        }
        if (authHeader.equalsIgnoreCase(AUTHENTICATION_SCHEME_BEARER)) {
            return Mono.error(new BadCredentialsException("Empty bearer authentication token"));
        }
        var token = authHeader.substring(TOKEN_START_POSITION);
        return Mono.just(new TokenAuthentication(token));
    }
}
