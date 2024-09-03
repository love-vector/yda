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

import org.springframework.boot.context.properties.ConfigurationProperties;

import ai.yda.framework.channel.shared.RestChannelProperties;

/**
 * Provides configuration properties for the REST Spring Channel. This class holds the configurable properties used in
 * the REST Channel. These properties can be customized through the application’s external configuration, such as
 * a properties file, YAML file, or environment variables. The properties include settings like the endpoint's relative
 * path and other configurations relevant to the REST API.
 * <p>
 * The properties are prefixed with {@link #CONFIG_PREFIX} and can be customized by defining values under this prefix
 * in the external configuration. Below are the properties that can be configured:
 * </p>
 * <pre>
 * Example configuration in a YAML file:
 *
 * ai:
 *   yda:
 *     framework:
 *       channel:
 *         rest:
 *           spring:
 *             endpointRelativePath: /api/v1
 *             securityToken: your-security-token
 * </pre>
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@ConfigurationProperties(RestSpringProperties.CONFIG_PREFIX)
public class RestSpringProperties extends RestChannelProperties {

    /**
     * The configuration prefix used to reference properties related to this Channel in application configurations.
     * This prefix is used for binding properties within the particular namespace.
     */
    public static final String CONFIG_PREFIX = "ai.yda.framework.channel.rest.spring";

    /**
     * Default constructor for {@link RestSpringProperties}.
     */
    public RestSpringProperties() {}
}
