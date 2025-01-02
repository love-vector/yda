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
package ai.yda.framework.rag.retriever.google_drive.autoconfigure;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

import ai.yda.framework.rag.retriever.google_drive.GoogleDriveRetriever;
import ai.yda.framework.rag.retriever.google_drive.exception.GoogleDriveException;
import ai.yda.framework.rag.retriever.google_drive.service.GoogleDriveService;

/**
 * Auto-configuration class for setting up the Google Drive retriever in a Spring Boot application.
 * This configuration automatically wires the necessary beans to enable the functionality
 * of retrieving data from Google Drive using the {@link GoogleDriveRetriever}.
 *
 * <p>The configuration is activated when the application includes the relevant starter
 * and the necessary properties are defined in the application configuration file.
 *
 * <p>Dependencies:
 * - Requires the {@link RetrieverGoogleDriveProperties} for configuration details such as
 *   the Service Account JSON file path, the `topK` retrieval parameter, and the processing flag.
 * - Requires a valid {@link ResourceLoader} to load the Service Account key file.
 *
 * <p>Usage:
 * - Ensure the application includes a properly configured `application.yml` or `application.properties` file
 *   with the required `google.drive.service-account-key-path`.
 * - The auto-configuration will provide a fully initialized {@link GoogleDriveRetriever} bean.
 *
 * @author dmmrch
 * @since 0.2.0
 */
@AutoConfiguration
@EnableConfigurationProperties(RetrieverGoogleDriveProperties.class)
public class RetrieverGoogleDriveAutoConfiguration {

    /**
     * Default constructor for {@link RetrieverGoogleDriveAutoConfiguration}.
     */
    public RetrieverGoogleDriveAutoConfiguration() {}

    @Bean
    public GoogleDriveRetriever googleDriveRetriever(
            final RetrieverGoogleDriveProperties googleDriveProperties, final ResourceLoader resourceLoader)
            throws IOException, GeneralSecurityException {

        var resource = resourceLoader.getResource(googleDriveProperties.getServiceAccountKeyFilePath());

        if (!resource.exists()) {
            throw new GoogleDriveException(String.format(
                    "Service Account key not found at: %s", googleDriveProperties.getServiceAccountKeyFilePath()));
        }

        return new GoogleDriveRetriever(
                googleDriveProperties.getTopK(),
                googleDriveProperties.getIsProcessingEnabled(),
                new GoogleDriveService(resource.getInputStream()));
    }
}
