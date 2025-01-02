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
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;

/**
 * Service class for interacting with Google Drive using a Service Account.
 * This service initializes using a Service Account JSON key provided as an {@link InputStream}.
 *
 * @author dmmrch
 * @since 0.2.0
 */
@Slf4j
public class GoogleDriveService {

    private static final String GOOGLE_DRIVE_APP_NAME = "YDA Google Drive Retriever";

    private final Drive driveService;

    /**
     * Constructs a new instance of {@link GoogleDriveService}.
     * Initializes the Google Drive API client using the provided Service Account JSON InputStream.
     *
     * @param credentialsStream the InputStream containing the Service Account JSON key file.
     * @throws IOException if an I/O error occurs while reading the Service Account key file.
     * @throws GeneralSecurityException if a security error occurs during Google API client initialization.
     */
    public GoogleDriveService(final @NonNull InputStream credentialsStream)
            throws IOException, GeneralSecurityException {

        var credentials =
                GoogleCredentials.fromStream(credentialsStream).createScoped(Collections.singleton(DriveScopes.DRIVE));

        driveService = new Drive.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        new HttpCredentialsAdapter(credentials))
                .setApplicationName(GOOGLE_DRIVE_APP_NAME)
                .build();

        listFiles();
        log.info("Google Drive service initialized successfully.");
    }

    public void listFiles() throws IOException {
        var result = driveService.files().list().setPageSize(10).execute();
        var files = result.getFiles();

        if (files == null || files.isEmpty()) {
            log.info("No files found.");
        } else {
            log.info("Files:");
            for (var file : files) {
                log.info("{} ({})", file.getName(), file.getId());
            }
        }
    }
}
