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
package ai.yda.framework.rag.retriever.google_drive.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;

@Slf4j
public class GoogleDriveService {

    private final Drive driveService;

    /**
     * Default constructor for {@link GoogleDriveService}.
     */
    public GoogleDriveService(
            final @NonNull String serviceAccountKeyFilePath, final @NonNull ResourceLoader resourceLoader)
            throws IOException, GeneralSecurityException {

        var keyFile = resourceLoader.getResource(serviceAccountKeyFilePath);

        if (!keyFile.exists()) {
            throw new RuntimeException("Service Account JSON file not found at: " + serviceAccountKeyFilePath);
        }

        var credentials = ServiceAccountCredentials.fromStream(keyFile.getInputStream())
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        driveService = new Drive.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        new HttpCredentialsAdapter(credentials))
                .setApplicationName("GoogleDriveTest")
                .build();

        listFiles();
    }

    public void listFiles() throws IOException {
        // List files from Google Drive
        var result = driveService.files().list().setPageSize(10).execute();
        var files = result.getFiles();

        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (var file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
    }
}
