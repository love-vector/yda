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
package ai.yda.framework.channel.rest.spring;

import ai.yda.framework.channel.rest.spring.config.QueryDeserializerConfig;
import ai.yda.framework.channel.rest.spring.security.SecurityConfiguration;
import ai.yda.framework.channel.rest.spring.service.SlackMessageService;
import ai.yda.framework.channel.rest.spring.session.RestSessionProvider;
import ai.yda.framework.channel.rest.spring.session.SessionContextHolder;
import ai.yda.framework.channel.rest.spring.web.SlackEventController;
import ai.yda.framework.session.core.ThreadLocalSessionProvider;
import com.slack.api.Slack;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AutoConfiguration
@EnableConfigurationProperties({RestSlackProperties.class})
@Import({
        SlackEventController.class,
        SecurityConfiguration.class,
        RestSessionProvider.class,
        QueryDeserializerConfig.class
})
public class RestSlackAutoConfiguration {
    /**
     * Default constructor for {@link RestSlackAutoConfiguration}.
     */
    @Bean
    public Slack slack() {
        return new Slack();
    }

    @Bean
    public SlackMessageService slackMessageService(Slack slack, RestSlackProperties restSlackProperties) {
        return new SlackMessageService(slack, restSlackProperties);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(20);
    }

    @Bean
    public SessionContextHolder sessionContextHolder() {
        return new SessionContextHolder();
    }

    @Bean
    public ThreadLocalSessionProvider threadLocalSessionProvider() {
        return new ThreadLocalSessionProvider();
    }

    public RestSlackAutoConfiguration() {
    }
}
