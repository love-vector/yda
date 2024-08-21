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

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationConverter authenticationConverter = new TokenAuthenticationConverter();

    private final TokenAuthenticationManager authenticationManager;

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    public TokenAuthenticationFilter(final String token) {
        this.authenticationManager = new TokenAuthenticationManager(token);
    }

    @Override
    protected void doFilterInternal(
            final @NonNull HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            var authRequest = this.authenticationConverter.convert(request);
            if (authRequest == null) {
                this.logger.trace("Did not process authentication request since failed"
                        + " to find token in Bearer Authorization header");
                filterChain.doFilter(request, response);
                return;
            }
            if (authenticationIsRequired(authRequest)) {
                var authResult = authenticationManager.authenticate(authRequest);
                securityContextHolderStrategy.getContext().setAuthentication(authResult);
            }
        } catch (final AuthenticationException ignored) {
        }
        filterChain.doFilter(request, response);
    }

    protected boolean authenticationIsRequired(final Authentication authentication) {
        // Only reauthenticate if token doesn't match SecurityContextHolder and user
        // isn't authenticated (see SEC-53)
        var currentAuthentication = securityContextHolderStrategy.getContext().getAuthentication();
        if (currentAuthentication == null
                || !currentAuthentication.getCredentials().equals(authentication.getCredentials())
                || !currentAuthentication.isAuthenticated()) {
            return true;
        }
        return (currentAuthentication instanceof AnonymousAuthenticationToken);
    }
}
