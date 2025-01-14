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
import java.time.Duration;

import com.zaxxer.hikari.HikariDataSource;

import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import ai.yda.framework.rag.retriever.google_drive.GoogleDriveRetriever;
import ai.yda.framework.rag.retriever.google_drive.adapter.DocumentContentAdapter;
import ai.yda.framework.rag.retriever.google_drive.adapter.DocumentMetadataAdapter;
import ai.yda.framework.rag.retriever.google_drive.exception.GoogleDriveException;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentContentMapper;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentContentMapperImpl;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentMetadataMapper;
import ai.yda.framework.rag.retriever.google_drive.mapper.DocumentMetadataMapperImpl;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentContentPort;
import ai.yda.framework.rag.retriever.google_drive.port.DocumentMetadataPort;
import ai.yda.framework.rag.retriever.google_drive.repository.DocumentContentRepository;
import ai.yda.framework.rag.retriever.google_drive.repository.DocumentMetadataRepository;
import ai.yda.framework.rag.retriever.google_drive.service.DocumentProcessorProvider;
import ai.yda.framework.rag.retriever.google_drive.service.DocumentSummaryService;
import ai.yda.framework.rag.retriever.google_drive.service.GoogleDriveService;
import ai.yda.framework.rag.retriever.google_drive.service.processor.ExelDocumentProcessor;
import ai.yda.framework.rag.retriever.google_drive.service.processor.TikaDocumentProcessor;

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
 * the Service Account JSON file path, the `topK` retrieval parameter, and the processing flag.
 * - Requires a valid {@link ResourceLoader} to load the Service Account key file.
 *
 * <p>Usage:
 * - Ensure the application includes a properly configured `application.yml` or `application.properties` file
 * with the required `google.drive.service-account-key-path`.
 * - The auto-configuration will provide a fully initialized {@link GoogleDriveRetriever} bean.
 *
 * @author dmmrch
 * @author Iryna Kopchak
 * @since 0.2.0
 */
@AutoConfiguration
@EnableConfigurationProperties(RetrieverGoogleDriveProperties.class)
@ComponentScan("ai.yda.framework.rag.retriever.google_drive")
@EnableJpaRepositories("ai.yda.framework.rag.retriever.google_drive.repository")
@EntityScan("ai.yda.framework.rag.retriever.google_drive.entity")
public class RetrieverGoogleDriveAutoConfiguration {

    /**
     * Default constructor for {@link RetrieverGoogleDriveAutoConfiguration}.
     */
    public RetrieverGoogleDriveAutoConfiguration() {}

    /**
     * Creates a {@link HikariDataSource} bean using properties defined under the prefix
     * "ai.yda.framework.rag.retriever.google-drive.database".
     *
     * @return a configured {@link HikariDataSource} instance.
     */
    @Bean
    @ConfigurationProperties("ai.yda.framework.rag.retriever.google-drive.database")
    public HikariDataSource dataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public DocumentMetadataPort documentMetadataPort(
            final DocumentMetadataRepository documentMetadataRepository,
            final DocumentMetadataMapper documentMetadataMapper,
            final DocumentContentMapper documentContentMapper) {
        return new DocumentMetadataAdapter(documentMetadataRepository, documentMetadataMapper, documentContentMapper);
    }

    @Bean
    public DocumentContentPort documentContentPort(
            final DocumentContentRepository documentContentRepository,
            final DocumentContentMapper documentContentMapper) {
        return new DocumentContentAdapter(documentContentRepository, documentContentMapper);
    }

    @Bean
    public DocumentContentMapper documentContentMapper() {
        return new DocumentContentMapperImpl();
    }

    @Bean
    public DocumentMetadataMapper documentMetadataMapper() {
        return new DocumentMetadataMapperImpl();
    }

    @Bean
    public ExelDocumentProcessor exelDocumentProcessor(final DocumentContentMapper documentContentMapper) {
        return new ExelDocumentProcessor(documentContentMapper);
    }

    @Bean
    public TikaDocumentProcessor tikaDocumentProcessor(final DocumentContentMapper documentContentMapper) {
        return new TikaDocumentProcessor(documentContentMapper);
    }

    @Bean
    public DocumentProcessorProvider documentProcessorProvider(
            final ExelDocumentProcessor exelDocumentProcessor, final TikaDocumentProcessor tikaDocumentProcessor) {
        return new DocumentProcessorProvider(exelDocumentProcessor, tikaDocumentProcessor);
    }

    @Bean
    public DocumentSummaryService documentSummaryService(
            final OpenAiConnectionProperties openAiConnectionProperties, final WebClient.Builder webClientBuilder) {
        var restClientBuilder = RestClient.builder()
                .requestFactory(ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(Duration.ofMinutes(5))
                        .withReadTimeout(Duration.ofMinutes(10))));
        // TODO Come up with another way to modify OpenAiChatModel
        var openAiApi = new OpenAiApi(
                openAiConnectionProperties.getBaseUrl(),
                openAiConnectionProperties.getApiKey(),
                restClientBuilder,
                webClientBuilder);

        return new DocumentSummaryService(new OpenAiChatModel(openAiApi));
    }

    @Bean
    public GoogleDriveRetriever googleDriveRetriever(
            final RetrieverGoogleDriveProperties googleDriveProperties,
            final ResourceLoader resourceLoader,
            final DocumentMetadataPort documentMetadataPort,
            final DocumentContentPort documentContentPort,
            final DocumentProcessorProvider documentProcessorProvider,
            final DocumentMetadataMapper documentMetadataMapper,
            final ChatClient.Builder chatClientBuilder,
            final OpenAiConnectionProperties openAiConnectionProperties,
            final WebClient.Builder webClientBuilder)
            throws IOException, GeneralSecurityException {

        var resource = resourceLoader.getResource(googleDriveProperties.getServiceAccountKeyFilePath());

        if (!resource.exists()) {
            throw new GoogleDriveException(String.format(
                    "Service Account key not found at: %s", googleDriveProperties.getServiceAccountKeyFilePath()));
        }

        return new GoogleDriveRetriever(
                googleDriveProperties.getTopK(),
                googleDriveProperties.getIsProcessingEnabled(),
                chatClientBuilder.build(),
                documentMetadataPort,
                new GoogleDriveService(
                        resource.getInputStream(),
                        googleDriveProperties.getDriveId(),
                        documentMetadataPort,
                        documentContentPort,
                        documentProcessorProvider,
                        documentMetadataMapper,
                        documentSummaryService(openAiConnectionProperties, webClientBuilder)));
    }
}
