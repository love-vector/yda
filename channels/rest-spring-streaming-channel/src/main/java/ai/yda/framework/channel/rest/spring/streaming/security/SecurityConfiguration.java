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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;

import ai.yda.framework.channel.rest.spring.streaming.RestSpringStreamingProperties;
import ai.yda.framework.channel.rest.spring.streaming.session.SessionHandlerFilter;

/**
 * This is a Web Flux Spring Security configuration that sets up security settings for the streaming REST Channel.
 *
 * @author Nikita Litvinov
 * @see RestSpringStreamingProperties
 * @since 0.1.0
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    /**
     * Default constructor for {@link SecurityConfiguration}.
     */
    public SecurityConfiguration() {}

    /**
     * This Channel security configuration is used when 'security-token' property is configured.
     * <p>
     * Defines security filters, user authentication mechanisms, and authorization rules to control access to the
     * Channel. This configuration includes settings for {@link TokenAuthenticationFilter}, HTTP security configurations
     * such as enabling or disabling CORS, disabling CSRF, and adding the {@link SessionHandlerFilter} after an
     * AnonymousAuthenticationWebFilter. It also specifies authorization rules: requests to the endpoint are authorized
     * and require authentication, while all other requests are not authorized and do not require authentication.
     * </p>
     *
     * @param http       the {@link ServerHttpSecurity} to configure.
     * @param properties the {@link RestSpringStreamingProperties} containing configuration properties for the security
     *                   setup.
     * @return a {@link SecurityWebFilterChain} instance configured with the specified HTTP security settings.
     */
    @Bean
    @ConditionalOnProperty(prefix = RestSpringStreamingProperties.CONFIG_PREFIX, name = "security-token")
    public SecurityWebFilterChain filterChain(
            final ServerHttpSecurity http, final RestSpringStreamingProperties properties) {
        var securityContextRepository = new WebSessionServerSecurityContextRepository();
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange.pathMatchers(properties.getEndpointRelativePath())
                        .authenticated()
                        .anyExchange()
                        .permitAll())
                .securityContextRepository(securityContextRepository)
                .addFilterAfter(
                        new TokenAuthenticationFilter(properties.getSecurityToken(), securityContextRepository),
                        SecurityWebFiltersOrder.ANONYMOUS_AUTHENTICATION);
        configureCors(http, properties);
        configureSessionManagement(http);
        return http.build();
    }

    /**
     * This Channel security configuration is used when the 'security-token' property is not configured.
     * <p>
     * This configuration disables CSRF protection and CORS, and sets up authorization rules such that all HTTP requests
     * are permitted without authentication.
     * </p>
     *
     * @param http       the {@link ServerHttpSecurity} to configure.
     * @param properties the {@link RestSpringStreamingProperties} containing CORS and session management configuration.
     * @return a {@link SecurityWebFilterChain} instance configured with the specified HTTP security settings.
     */
    @Bean
    @ConditionalOnMissingBean
    public SecurityWebFilterChain defaultFilterChain(
            final ServerHttpSecurity http, final RestSpringStreamingProperties properties) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                .securityContextRepository(new WebSessionServerSecurityContextRepository());
        configureCors(http, properties);
        configureSessionManagement(http);
        return http.build();
    }

    /**
     * Configures CORS with provided properties.
     *
     * @param http       the {@link HttpSecurity} to configure.
     * @param properties for configuring CORS
     */
    private void configureCors(final ServerHttpSecurity http, final RestSpringStreamingProperties properties) {
        if (properties.getCorsEnabled()) {
            http.cors(cors -> {
                var config = new CorsConfiguration();
                config.setAllowedOrigins(properties.getAllowedOrigins());
                config.setAllowedMethods(properties.getAllowedMethods());
                config.setAllowCredentials(true);
                config.addAllowedHeader("*");
                cors.configurationSource(request -> config);
            });
        } else {
            http.cors(ServerHttpSecurity.CorsSpec::disable);
        }
    }

    /**
     * Adds the {@link SessionHandlerFilter} after an AnonymousAuthenticationWebFilter to handle session creation.
     *
     * @param http the {@link ServerHttpSecurity} to configure.
     */
    private void configureSessionManagement(final ServerHttpSecurity http) {
        http.addFilterAfter(new SessionHandlerFilter(), SecurityWebFiltersOrder.ANONYMOUS_AUTHENTICATION);
    }
}
