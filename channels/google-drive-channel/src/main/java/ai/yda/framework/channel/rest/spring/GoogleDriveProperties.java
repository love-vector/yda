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

import ai.yda.framework.channel.shared.RestChannelProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(GoogleDriveProperties.CONFIG_PREFIX)
public class GoogleDriveProperties {

    public static final String CONFIG_PREFIX = "ai.yda.framework.channel.google.drive";

    private String endpointRelativePath = RestChannelProperties.DEFAULT_ENDPOINT_RELATIVE_PATH;

    private String securityToken;

    private Boolean corsEnabled = Boolean.TRUE;

    private List<String> allowedOrigins = List.of("*");

    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE");

    private String serviceAccountKeyFilePath;

    private String driveId;
}
