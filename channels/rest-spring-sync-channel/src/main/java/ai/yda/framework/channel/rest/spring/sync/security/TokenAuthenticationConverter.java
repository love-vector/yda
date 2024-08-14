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
package ai.yda.framework.channel.rest.spring.sync.security;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class TokenAuthenticationConverter implements AuthenticationConverter {

    private static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";

    private static final Short TOKEN_START_POSITION = 7;

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

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
        var currentAuthentication = securityContextHolderStrategy.getContext().getAuthentication();
        return new TokenAuthentication(
                token, currentAuthentication.getPrincipal(), currentAuthentication.getAuthorities());
    }
}
