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
package ai.yda.framework.channel.rest.spring.security;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

import ai.yda.framework.channel.shared.TokenAuthentication;

/**
 * Provides strategy used for converting from a {@link HttpServletRequest} to an {@link TokenAuthentication}. Used to
 * authenticate with {@link TokenAuthenticationManager}. If the result is null, then it signals that no authentication
 * attempt should be made.
 *
 * @author Nikita Litvinov
 * @see TokenAuthentication
 * @since 0.1.0
 */
public class TokenAuthenticationConverter implements AuthenticationConverter {
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
     * Converts the given {@link HttpServletRequest} to an {@link TokenAuthentication}. If the result is null, then it
     * signals that no authentication attempt should be made.
     *
     * @param request the {@link HttpServletRequest} to be converted.
     * @return the {@link Authentication} that is a result of conversion or null if there is no authentication.
     */
    @Override
    public Authentication convert(final HttpServletRequest request) {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            return null;
        }
        authHeader = authHeader.trim();
        if (!StringUtils.startsWithIgnoreCase(authHeader, AUTHENTICATION_SCHEME_BEARER)) {
            return null;
        }
        if (authHeader.equalsIgnoreCase(AUTHENTICATION_SCHEME_BEARER)) {
            throw new BadCredentialsException("Empty bearer authentication token");
        }
        var token = authHeader.substring(TOKEN_START_POSITION);
        var currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        return currentAuthentication == null
                ? new TokenAuthentication(token)
                : new TokenAuthentication(
                        token, currentAuthentication.getPrincipal(), currentAuthentication.getAuthorities());
    }
}
