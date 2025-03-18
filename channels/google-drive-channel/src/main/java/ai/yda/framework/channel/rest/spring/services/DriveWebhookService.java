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
package ai.yda.framework.channel.rest.spring.services;

import java.io.IOException;

import ai.yda.framework.channel.rest.spring.GoogleDriveProperties;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Channel;

import org.springframework.stereotype.Service;

@Service
public class DriveWebhookService {
    private final Drive driveService;
    private final GoogleDriveProperties properties;
    private static final String WEBHOOK_RECEIVER_URL = "https://your-domain.com/google-drive/webhook";


    public DriveWebhookService(Drive driveService, GoogleDriveProperties properties) {
        this.driveService = driveService;
        this.properties = properties;
    }

    public Channel watchChanges() {
        Channel channel =
                new Channel().setId(properties.getDriveId()).setType("web_hook").setAddress(WEBHOOK_RECEIVER_URL);
        try {
            return driveService.changes().watch(getStartPageToken(), channel).execute();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка подписки на события Google Drive", e);
        }
    }

    private String getStartPageToken() throws IOException {
        return driveService.changes().getStartPageToken().execute().getStartPageToken();
    }
}
