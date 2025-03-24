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
package ai.yda.framework.channel.rest.spring.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
}
/*    private final Drive driveService;
private final GoogleDriveProperties googleDriveProperties;
private String savedStartPageToken;

@Autowired
public DriveWebhookService(Drive driveService, GoogleDriveProperties googleDriveProperties) {
    this.driveService = driveService;
    this.googleDriveProperties = googleDriveProperties;
}

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
}*/
