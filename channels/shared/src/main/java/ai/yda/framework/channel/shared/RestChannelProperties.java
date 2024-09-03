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
package ai.yda.framework.channel.shared;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class providing common properties for REST Channel configurations.
 *
 * @author Nikita Litvinov
 * @since 0.1.0
 */
@Setter
@Getter
public abstract class RestChannelProperties {

    /**
     * The default relative path for the REST endpoint.
     */
    public static final String DEFAULT_ENDPOINT_RELATIVE_PATH = "/";

    /**
     * The relative path for the REST endpoint. This path can be customized based on specific configurations.
     * By default, it is set to {@link #DEFAULT_ENDPOINT_RELATIVE_PATH}.
     */
    private String endpointRelativePath = RestChannelProperties.DEFAULT_ENDPOINT_RELATIVE_PATH;

    /**
     * The security token used for authenticating requests to the REST endpoint.
     * This token is expected to be provided by the client for authorization purposes.
     */
    private String securityToken;

    /**
     * Enable or disable CORS.
     */
    private Boolean corsEnabled = Boolean.TRUE;

    /**
     * List of allowed origins for CORS.
     */
    private List<String> allowedOrigins = List.of("*");

    /**
     * List of allowed methods for CORS.
     */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE");
}
