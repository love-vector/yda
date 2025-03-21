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
package ai.yda.framework.rag.retriever.google_drive.controller;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.yda.framework.rag.retriever.google_drive.service.DriveWebhookService;

@Slf4j
@RestController
@RequestMapping("/google-drive/webhook")
@RequiredArgsConstructor
public class GoogleDriveChannel {
    private final DriveWebhookService driveWebhookService;

    @PostMapping
    public ResponseEntity<Void> handleWebhook(HttpServletRequest request) {
        var resourceState = request.getHeader("X-Goog-Resource-State");
        var channelId = request.getHeader("X-Goog-Channel-Id");
        var resourceId = request.getHeader("X-Goog-Resource-Id");

        log.info("Webhook received: state={}, channelId={}, resourceId={}", resourceState, channelId, resourceId);

        if (resourceState == null || resourceState.isBlank()) {
            log.warn("Webhook received without X-Goog-Resource-State");
            return ResponseEntity.badRequest().build();
        }

        try {
            driveWebhookService.processWebhook();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error while processing webhook", e);
            return ResponseEntity.accepted().build();
        }
    }
}
