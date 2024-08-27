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

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import ai.yda.framework.channel.rest.spring.RestSpringProperties;

/**
 * This is a Spring Security configuration that sets up security settings for the synchronized REST Channel.
 *
 * @author Nikita Litvinov
 * @see RestSpringProperties
 * @since 0.1.0
 */
@Configuration
@EnableWebSecurity
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
     * such as disabling CORS, disabling CSRF, and setting the session management creation policy to always. It also
     * specifies authorization rules: requests to the endpoint are authorized and require authentication, while all
     * other requests are not authorized and do not require authentication.
     * </p>
     *
     * @param http       the {@link HttpSecurity} to configure.
     * @param properties the {@link RestSpringProperties} containing configuration properties for the security setup.
     * @return a {@link SecurityFilterChain} instance configured with the specified HTTP security settings.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    @ConditionalOnProperty(prefix = RestSpringProperties.CONFIG_PREFIX, name = "security-token")
    public SecurityFilterChain filterChain(final HttpSecurity http, final RestSpringProperties properties)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(properties.getEndpointRelativePath())
                        .authenticated()
                        .anyRequest()
                        .permitAll())
                .addFilterAfter(
                        new TokenAuthenticationFilter(properties.getSecurityToken()),
                        AnonymousAuthenticationFilter.class);
        configureSessionManagement(http);
        return http.build();
    }

    /**
     * This Channel security configuration is used when 'security-token' property is not configured.
     * <p>
     * This configuration disables CSRF protection and CORS, and sets up authorization rules such that all HTTP requests
     * are permitted without authentication.
     * </p>
     *
     * @param http the {@link HttpSecurity} to configure.
     * @return a {@link SecurityFilterChain} instance configured with the specified HTTP security settings.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain defaultFilterChain(final HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
        configureSessionManagement(http);
        return http.build();
    }

    /**
     * Configures session management to always create a session and limits the number of sessions to 1.
     *
     * @param http the {@link HttpSecurity} to configure.
     * @throws Exception if an error occurs during configuration.
     */
    private void configureSessionManagement(final HttpSecurity http) throws Exception {
        http.sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .maximumSessions(1));
    }
}
