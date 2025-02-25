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
package ai.yda.framework.slack.channel;

import com.slack.api.Slack;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import ai.yda.framework.session.core.InMemorySessionStore;
import ai.yda.framework.session.core.SessionStore;
import ai.yda.framework.session.core.ThreadLocalSessionContext;
import ai.yda.framework.session.core.ThreadLocalSessionProvider;
import ai.yda.framework.slack.channel.servlet.SlackOAuthInstallController;
import ai.yda.framework.slack.channel.servlet.SlackOAuthRedirectController;

@AutoConfiguration
@ComponentScan
@ServletComponentScan
@EnableConfigurationProperties({SlackProperties.class})
public class SlackAutoConfiguration {
    /**
     * Default constructor for {@link SlackAutoConfiguration}.
     */
    @Bean
    public Slack slack() {
        return new Slack();
    }

    @Bean
    public ThreadLocalSessionContext sessionContext() {
        return new ThreadLocalSessionContext();
    }

    @Bean
    public SessionStore sessionStore() {
        return new InMemorySessionStore();
    }

    @Bean
    public ThreadLocalSessionProvider threadContext(
            final SessionStore sessionStore, final ThreadLocalSessionContext sessionContext) {
        return new ThreadLocalSessionProvider(sessionStore, sessionContext);
    }

    @Bean
    public App initSlackApp(final SlackProperties properties) {
        var config = AppConfig.builder()
                .signingSecret(properties.getSigningSecret())
                .clientId(properties.getClientId())
                .clientSecret(properties.getClientSecret())
                .oauthInstallPath(SlackOAuthInstallController.URL_PATTERN)
                .oauthRedirectUriPath(SlackOAuthRedirectController.URL_PATTERN)
                .scope("commands,chat:write.public,chat:write")
                .build();

        return new App(config).asOAuthApp(true);
    }

    public SlackAutoConfiguration() {}
}
