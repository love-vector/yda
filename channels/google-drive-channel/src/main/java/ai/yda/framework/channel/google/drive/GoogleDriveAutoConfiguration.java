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
package ai.yda.framework.channel.google.drive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;

import ai.yda.framework.channel.google.drive.config.SecurityConfig;
import ai.yda.framework.channel.google.drive.deserializer.QueryDeserializerConfig;
import ai.yda.framework.channel.google.drive.services.DriveWebhookService;
import ai.yda.framework.channel.google.drive.session.RestSessionProvider;
import ai.yda.framework.channel.google.drive.web.GoogleDriveChannel;

@AutoConfiguration
@EnableConfigurationProperties({GoogleDriveProperties.class})
@Import({
    GoogleDriveChannel.class,
    DriveWebhookService.class,
    QueryDeserializerConfig.class,
    RestSessionProvider.class,
    SecurityConfig.class
})
public class GoogleDriveAutoConfiguration {

    private static final String GOOGLE_DRIVE_APP_NAME = "YDA Google Drive Channel";
    private final GoogleDriveProperties googleDriveProperties;
    private final ResourceLoader resourceLoader;

    @Autowired
    public GoogleDriveAutoConfiguration(GoogleDriveProperties googleDriveProperties, ResourceLoader resourceLoader) {
        this.googleDriveProperties = googleDriveProperties;
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public Drive drive() throws GeneralSecurityException, IOException {
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        InputStream in = resourceLoader
                .getResource(googleDriveProperties.getServiceAccountKeyFilePathOauth2())
                .getInputStream();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

        // Указываем права, необходимые для работы с Google Drive
        List<String> scopes = List.of(DriveScopes.DRIVE);

        // Создаём OAuth-авторизацию
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                        httpTransport, jsonFactory, clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens")))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8888) // Фиксируем порт 8888
                .build();

        // Открываем локальный OAuth-сервер
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("YDA Google Drive Channel")
                .build();
    }
}
