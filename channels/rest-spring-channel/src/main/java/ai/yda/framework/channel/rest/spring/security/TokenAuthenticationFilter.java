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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Provides a Spring Security filter that processes authentication requests containing a Bearer token in the
 * Authorization header. The filter converts the Bearer token into an {@link Authentication} object and attempts to
 * authenticate it using the {@link TokenAuthenticationManager}. If the authentication is successful, the authenticated
 * user is stored in the {@link SecurityContextHolder}.
 *
 * @author Nikita Litvinov
 * @see TokenAuthenticationConverter
 * @see TokenAuthenticationManager
 * @since 0.1.0
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationConverter authenticationConverter = new TokenAuthenticationConverter();

    private final TokenAuthenticationManager authenticationManager;

    /**
     * Constructs a new {@link TokenAuthenticationFilter} instance with the provided token.
     *
     * @param token the token used to authenticate incoming requests.
     */
    public TokenAuthenticationFilter(final @NonNull String token) {
        this.authenticationManager = new TokenAuthenticationManager(token);
    }

    /**
     * Processes the authentication request by extracting the Bearer token from the Authorization header, converting it
     * to an {@link Authentication} object, and attempting to authenticate it. The request and response is then
     * forwarded to the next filter in the chain.
     *
     * @param request     the current {@link HttpServletRequest}.
     * @param response    the current {@link HttpServletResponse}.
     * @param filterChain the {@link FilterChain} to proceed with the request processing
     * @throws ServletException if an error occurs during the filtering process.
     * @throws IOException      if an I/O error occurs during the filtering process.
     */
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
                SecurityContextHolder.getContext().setAuthentication(authResult);
            }
        } catch (final AuthenticationException ignored) {
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Determines whether re-authentication is required based on the current authentication context.
     *
     * @param authentication the new {@link Authentication} request to be processed.
     * @return {@code true} if re-authentication is required and {@code false} otherwise.
     */
    protected boolean authenticationIsRequired(final Authentication authentication) {
        // Only reauthenticate if token doesn't match SecurityContextHolder and user
        // isn't authenticated (see SEC-53)
        var currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuthentication == null
                || !currentAuthentication.getCredentials().equals(authentication.getCredentials())
                || !currentAuthentication.isAuthenticated()) {
            return true;
        }
        return (currentAuthentication instanceof AnonymousAuthenticationToken);
    }
}
