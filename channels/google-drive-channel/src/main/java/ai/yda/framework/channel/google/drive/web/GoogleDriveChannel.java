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
package ai.yda.framework.channel.google.drive.web;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.yda.framework.channel.google.drive.services.DriveWebhookService;

@Slf4j
@RestController
@RequestMapping("/google-drive/webhook")
@RequiredArgsConstructor
public class GoogleDriveChannel {

    private final DriveWebhookService driveWebhookService;

    @PostMapping
    public ResponseEntity<Void> handleWebhook(HttpServletRequest request) {
        var resourceState = request.getHeader("X-Goog-Resource-State");
        log.info("Webhook notification received. State={}", resourceState);

        if ("sync".equals(resourceState)) {
            log.info("Sync notification received.");
            return ResponseEntity.ok().build();
        }

        driveWebhookService.processWebhook();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-changes")
    public ResponseEntity<String> checkChanges() {
        String result = driveWebhookService.checkChanges();
        return ResponseEntity.ok(result);
    }
}
