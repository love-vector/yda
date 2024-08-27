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

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import ai.yda.framework.channel.rest.spring.security.SecurityConfiguration;
import ai.yda.framework.channel.rest.spring.session.RestSessionProvider;
import ai.yda.framework.channel.rest.spring.web.RestChannel;

/**
 * Contains an autoconfiguration for the REST Channel in the Spring application. This class is responsible for
 * automatically configuring the necessary beans and components for the REST Channel, security and session management.
 * It simplifies the setup by ensuring that all the required configurations, properties, and components are loaded and
 * initialized without needing explicit configuration by the developer. It is loaded, initialized and imported without
 * developer's interference
 *
 * @author Nikita Litvinov
 * @see RestSpringProperties
 * @see RestChannel
 * @see SecurityConfiguration
 * @see RestSessionProvider
 * @since 0.1.0
 */
@AutoConfiguration
@EnableConfigurationProperties({RestSpringProperties.class})
@Import({RestChannel.class, SecurityConfiguration.class, RestSessionProvider.class})
public class RestSpringAutoConfiguration {
    /**
     * Default constructor for {@link RestSpringAutoConfiguration}.
     */
    public RestSpringAutoConfiguration() {}
}
