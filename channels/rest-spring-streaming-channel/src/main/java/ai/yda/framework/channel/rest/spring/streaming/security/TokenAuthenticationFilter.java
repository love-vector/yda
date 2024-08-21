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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import ai.yda.framework.channel.shared.TokenAuthenticationException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter implements WebFilter {

    private final TokenAuthenticationConverter authenticationConverter = new TokenAuthenticationConverter();

    private final TokenAuthenticationManager authenticationManager;

    private final ServerSecurityContextRepository securityContextRepository;

    private final ServerAuthenticationSuccessHandler authenticationSuccessHandler =
            new WebFilterChainServerAuthenticationSuccessHandler();

    private final ServerAuthenticationFailureHandler authenticationFailureHandler =
            new ServerAuthenticationEntryPointFailureHandler(new HttpBasicServerAuthenticationEntryPoint());

    public TokenAuthenticationFilter(
            final String token, final ServerSecurityContextRepository securityContextRepository) {
        this.authenticationManager = new TokenAuthenticationManager(token);
        this.securityContextRepository = securityContextRepository;
    }

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull final ServerWebExchange exchange, @NonNull final WebFilterChain chain) {
        return this.authenticationConverter
                .convert(exchange)
                .switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
                .flatMap(authenticationManager::authenticate)
                .flatMap(authentication ->
                        onAuthenticationSuccess(authentication, new WebFilterExchange(exchange, chain)))
                .onErrorResume(
                        TokenAuthenticationException.class,
                        (exception) -> this.authenticationFailureHandler.onAuthenticationFailure(
                                new WebFilterExchange(exchange, chain), exception));
    }

    protected Mono<Void> onAuthenticationSuccess(
            final Authentication authentication, final WebFilterExchange webFilterExchange) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);
        return this.securityContextRepository
                .save(exchange, securityContext)
                .then(this.authenticationSuccessHandler.onAuthenticationSuccess(webFilterExchange, authentication))
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
    }
}
