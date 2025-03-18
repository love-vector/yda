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
import ai.yda.framework.channel.rest.spring.services.DriveWebhookService;
import ai.yda.framework.channel.rest.spring.session.RestSessionProvider;
import ai.yda.framework.channel.rest.spring.web.GoogleDriveChannel;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@AutoConfiguration
@EnableConfigurationProperties({GoogleDriveProperties.class})
@Import({
        GoogleDriveChannel.class,
        RestSessionProvider.class,
        QueryDeserializerConfig.class,
        DriveWebhookService.class
})
public class GoogleDriveAutoConfiguration {
    private static final String GOOGLE_DRIVE_APP_NAME = "YDA Google Drive Channel";
    private GoogleDriveProperties googleDriveProperties;

    @Autowired
    private ResourceLoader resourceLoader;

    @Bean
    public Drive drive() throws GeneralSecurityException, IOException {
        var resource = resourceLoader.getResource(googleDriveProperties.getServiceAccountKeyFilePath());
        var credentialsStream = resource.getInputStream();
        var credentials = GoogleCredentials.fromStream(credentialsStream).createScoped(Collections.singleton(DriveScopes.DRIVE));
        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(GOOGLE_DRIVE_APP_NAME)
                .build();
    }

    public GoogleDriveAutoConfiguration() {
    }
}
