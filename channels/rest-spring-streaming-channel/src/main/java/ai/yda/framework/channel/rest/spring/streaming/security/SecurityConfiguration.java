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

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

import ai.yda.framework.channel.rest.spring.streaming.RestSpringStreamingProperties;
import ai.yda.framework.channel.rest.spring.streaming.session.SessionHandlerFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = RestSpringStreamingProperties.CONFIG_PREFIX, name = "security-token")
    public SecurityWebFilterChain filterChain(
            final ServerHttpSecurity http, final RestSpringStreamingProperties properties) {
        var securityContextRepository = new WebSessionServerSecurityContextRepository();
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(exchange -> exchange.pathMatchers(properties.getEndpointRelativePath())
                        .authenticated()
                        .anyExchange()
                        .permitAll())
                .securityContextRepository(securityContextRepository)
                .addFilterAfter(
                        new TokenAuthenticationFilter(properties.getSecurityToken(), securityContextRepository),
                        SecurityWebFiltersOrder.ANONYMOUS_AUTHENTICATION);
        configureSessionManagement(http);
        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityWebFilterChain defaultFilterChain(final ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                .securityContextRepository(new WebSessionServerSecurityContextRepository());
        configureSessionManagement(http);
        return http.build();
    }

    private void configureSessionManagement(final ServerHttpSecurity http) {
        http.addFilterAfter(new SessionHandlerFilter(), SecurityWebFiltersOrder.ANONYMOUS_AUTHENTICATION);
    }
}
