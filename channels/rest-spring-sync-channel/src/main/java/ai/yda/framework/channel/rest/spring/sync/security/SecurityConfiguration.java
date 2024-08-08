package ai.yda.framework.channel.rest.spring.sync.security;

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

import ai.yda.framework.channel.rest.spring.sync.RestSpringSyncProperties;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = RestSpringSyncProperties.CONFIG_PREFIX, name = "security-token")
    public SecurityFilterChain filterChain(final HttpSecurity http, final RestSpringSyncProperties properties)
            throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(properties.getEndpointRelativePath())
                        .authenticated()
                        .anyRequest()
                        .permitAll())
                .addFilterAfter(
                        new TokenAuthenticationFilter(properties.getSecurityToken()),
                        AnonymousAuthenticationFilter.class)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .maximumSessions(1))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain defaultFilterChain(final HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .build();
    }
}
