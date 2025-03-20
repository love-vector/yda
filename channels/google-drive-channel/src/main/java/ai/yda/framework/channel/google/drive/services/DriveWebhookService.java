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
package ai.yda.framework.channel.google.drive.services;

import ai.yda.framework.channel.google.drive.GoogleDriveProperties;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Channel;
import com.google.api.services.drive.model.StartPageToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriveWebhookService {

    private final Drive driveService;
    private final GoogleDriveProperties googleDriveProperties;
    private String savedStartPageToken;
    private Set<String> documentToUpdate = new HashSet<>();

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        initializeStartPageToken();
        setupDriveWatchChannel();
    }

    private void initializeStartPageToken() {
        try {
            StartPageToken response = driveService
                    .changes()
                    .getStartPageToken()
                    .setDriveId(googleDriveProperties.getDriveId())
                    .setSupportsAllDrives(true)
                    .execute();

            this.savedStartPageToken = response.getStartPageToken();
            log.info("Obtained initial startPageToken: {}", savedStartPageToken);

        } catch (Exception e) {
            log.error("Failed to get initial startPageToken", e);
        }
    }

    private void setupDriveWatchChannel() {
        Channel channel = new Channel()
                .setId(UUID.randomUUID().toString())
                .setType("web_hook")
                .setAddress(googleDriveProperties.getWebhookReceiverUrl())
                .setExpiration(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));

        try {
            driveService
                    .changes()
                    .watch(savedStartPageToken, channel)
                    .setDriveId(googleDriveProperties.getDriveId())
                    .setIncludeItemsFromAllDrives(true)
                    .setSupportsAllDrives(true)
                    .execute();

            log.info("Subscribed successfully to Google Drive webhook (Shared Drive).");

        } catch (Exception e) {
            log.error("Error subscribing to Google Drive webhook", e);
        }
    }

    public void processWebhook() {
        try {
            var changes = driveService
                    .changes()
                    .list(savedStartPageToken)
                    .setDriveId(googleDriveProperties.getDriveId())
                    .setSupportsAllDrives(true)
                    .setIncludeItemsFromAllDrives(true)
                    .execute();

            for (var change : changes.getChanges()) {

                documentToUpdate.add(change.getFileId());
                log.info("Changed fileId: {}, Removed: {}", change.getFileId(), change.getRemoved());
            }

            savedStartPageToken =
                    Optional.ofNullable(changes.getNewStartPageToken()).orElse(savedStartPageToken);

        } catch (Exception e) {
            log.error("Error handling changes", e);
        }
    }

    public String checkChanges() {
        try {
            var changes = driveService
                    .changes()
                    .list(savedStartPageToken)
                    .setDriveId(googleDriveProperties.getDriveId())
                    .setSupportsAllDrives(true)
                    .setIncludeItemsFromAllDrives(true)
                    .execute();

            StringBuilder sb = new StringBuilder("Changes detected:\n");
            for (var change : changes.getChanges()) {
                sb.append("File ID: ")
                        .append(change.getFileId())
                        .append(", Removed: ")
                        .append(change.getRemoved())
                        .append("\n");
            }

            savedStartPageToken =
                    Optional.ofNullable(changes.getNewStartPageToken()).orElse(savedStartPageToken);
            return sb.toString();

        } catch (Exception e) {
            log.error("Error fetching changes", e);
            return "Error fetching changes";
        }
    }
}
