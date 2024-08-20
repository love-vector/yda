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
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter implements WebFilter {

    private final TokenAuthenticationConverter authenticationConverter = new TokenAuthenticationConverter();

    private final TokenAuthenticationManager authenticationManager;

    public TokenAuthenticationFilter(final String token) {
        this.authenticationManager = new TokenAuthenticationManager(token);
    }

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull final ServerWebExchange exchange, @NonNull final WebFilterChain chain) {
        return this.authenticationConverter
                .convert(exchange)
                .flatMap(authentication -> authenticationIsRequired(authentication)
                        .flatMap(isRequired ->
                                isRequired ? authenticationManager.authenticate(authentication) : Mono.empty()))
                .flatMap(authentication -> ReactiveSecurityContextHolder.getContext()
                        .flatMap(securityContext -> {
                            securityContext.setAuthentication(authentication);
                            return Mono.empty();
                        }))
                .then(chain.filter(exchange));
    }

    protected Mono<Boolean> authenticationIsRequired(final Authentication authentication) {
        // Only reauthenticate if token doesn't match SecurityContextHolder and user
        // isn't authenticated (see SEC-53)
        return ReactiveSecurityContextHolder.getContext().flatMap(context -> {
            var currentAuthentication = context.getAuthentication();
            if (currentAuthentication == null
                    || !currentAuthentication.getCredentials().equals(authentication.getCredentials())
                    || !currentAuthentication.isAuthenticated()) {
                return Mono.just(Boolean.TRUE);
            }
            return Mono.just(currentAuthentication instanceof AnonymousAuthenticationToken);
        });
    }
}
